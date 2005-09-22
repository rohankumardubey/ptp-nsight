/******************************************************************************
 * Copyright (c) 2005 The Regents of the University of California.
 * This material was produced under U.S. Government contract W-7405-ENG-36
 * for Los Alamos National Laboratory, which is operated by the University
 * of California for the U.S. Department of Energy. The U.S. Government has
 * rights to use, reproduce, and distribute this software. NEITHER THE
 * GOVERNMENT NOR THE UNIVERSITY MAKES ANY WARRANTY, EXPRESS OR IMPLIED, OR
 * ASSUMES ANY LIABILITY FOR THE USE OF THIS SOFTWARE. If software is modified
 * to produce derivative works, such modified software should be clearly  
 * marked, so as not to confuse it with the version available from LANL.
 *
 * Additionally, this program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * LA-CC 04-115
 ******************************************************************************/

/*
 * Client runs as task id num_procs, where num_procs are the number of processes
 * in the job being debugged, and is responsible for coordinating protocol
 * messages between the debug client interface (whatever that may be)
 * and the debug servers.
 * 
 * Note that there will be num_procs+1 [0..num_procs] processes in our 
 * communicator, where num_procs is the number of processes in the parallel 
 * job being debugged. To simplify the accounting, we use the task id of
 * num_procs as the client task id and [0..num_procs-1] for the server
 * task ids.
 */

#include <mpi.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include "dbg.h"
#include "dbg_client.h"
#include "procset.h"
#include "list.h"
#include "hash.h"

/*
 * A request represents an asynchronous send/receive transaction between the client
 * and all servers. completed() is called once all replys have been received.
 */
struct active_request {
	procset *		procs;
	void				(*completed)(Hash *);
	Hash *			events;
};
typedef struct active_request	active_request;

static procset *		sending_procs;
static procset *		receiving_procs;
static List *		active_requests;
static char **		send_bufs;
static MPI_Request *	send_requests;
int *				pids;
MPI_Status *			stats;

int num_servers;
int my_task_id;

/*
 * Send a command to the servers specified in procset. 
 * 
 * It is permissible to have multiple outstanding commands, provided
 * the processes each command applies to are disjoint sets.
 */
int
send_command(procset *procs, char *str, void (*completed_callback)(Hash *))
{
	int				pid;
	int				cmd_len;
	procset *		p;
	active_request *	r;
	
	/*
	 * Check if any processes already have active requests
	 */
	p = procset_and(sending_procs, procs);
	procset_andeq(p, receiving_procs);
	if (!procset_isempty(p)) {
		DbgSetError(DBGERR_INPROGRESS, NULL);
		return -1;
	}
	
	procset_free(p);
	/*
	 * Update sending processes
	 */	
	procset_oreq(sending_procs, procs);

	/*
	 * Create a new request and add it too the active list
	 */
	r = (active_request *)malloc(sizeof(active_request));
	r->procs = procset_copy(procs);
	r->completed = completed_callback;
	r->events = HashCreate(procset_size(procs)); // TODO: Check this is a sensible size
	
	AddToList(active_requests, (void *)r);

	/*
	 * Now post commands to the servers
	 */
	for (pid = 0; pid < num_servers; pid++) {
		if (procset_test(procs, pid)) {
			/*
			 * MPI spec does not allow read access to a send buffer while send is in progress
			 * so we must make a copy for each send.
			 */
			send_bufs[pid] = strdup(str);
			cmd_len = strlen(str);
			
			MPI_Isend(send_bufs[pid], cmd_len, MPI_CHAR, pid, 0, MPI_COMM_WORLD, &send_requests[pid]); // TODO: handle fatal errors
		}
	}

	return 0;
}

/*
 * Check for any replies from servers. If any are received, and these complete a send request,
 * then processes the reply.
 */
void
progress_commands(void)
{
	int				i;
	int				avail;
	int				recv_pid;
	int 				completed;
	char *			reply_buf;
	unsigned int		count;
	unsigned int		hdr[2];
	active_request *	r;
	MPI_Status		stat;
	dbg_event *		e;

	/*
	 * Check for completed sends
	 */
	count = procset_size(sending_procs);
	if (count > 0) {
		if (MPI_Testsome(num_servers, send_requests, &completed, pids, stats) != MPI_SUCCESS) {
			printf("error in testsome\n");
			exit(1);
		}
		
		for (i = 0; i < completed; i++) {
			procset_remove_proc(sending_procs, pids[i]);
			procset_add_proc(receiving_procs, pids[i]);
			free(send_bufs[pids[i]]);
		}
	}
		
	/*
	 * Check for replys
	 */
	count = procset_size(receiving_procs);
	if (count > 0) {
		MPI_Iprobe(MPI_ANY_SOURCE, 0, MPI_COMM_WORLD, &avail, &stat);
	
		if (avail == 0)
			return;
		
		/*
		 * A message is available, so receive it
		 * 
		 * A message is split into two parts: a header comprising two
		 * unsigned integers (a hash value and a length); a body which 
		 * is the dbg_event structure converted to a string.
		 * 
		 * The hash is computed by each server and is used to quickly
		 * coalesce events.
		 * 
		 * The length is the length of the event string.
		 * 
		 */
		//MPI_Get_count(&stat, MPI_CHAR, &count);
		
		recv_pid = stat.MPI_SOURCE;		
		
		MPI_Recv(hdr, 2, MPI_UNSIGNED, recv_pid, 0, MPI_COMM_WORLD, &stat);

printf("got header <0x%0x,%d> from [%d]\n", hdr[0], hdr[1], recv_pid);	
	
		count = hdr[1];
		reply_buf = (char *)malloc(count + 1);
		
		MPI_Recv(reply_buf, count, MPI_CHAR, recv_pid, 0, MPI_COMM_WORLD, &stat);
		reply_buf[count] = '\0';
printf("got reply <%s> from [%d]\n", reply_buf, recv_pid);		
		
		/*
		 * Find request for this proc
		 */
		for (SetList(active_requests); (r = (active_request *)GetListElement(active_requests)) != NULL; ) {
			if (procset_test(r->procs, recv_pid)) {
				/*
				 * Save event if it is new, otherwise just add this process to the event
				 */
				if ((e = HashSearch(r->events, hdr[0])) == NULL) {
					proxy_tcp_str_to_event(reply_buf, &e);
					e->procs = procset_new(num_servers);
					HashInsert(r->events, hdr[0], (void *)e);
				}
				
				procset_add_proc(e->procs, recv_pid);
								
				/*
				 * Call notify function if all receives have been completed
				 */
				procset_remove_proc(r->procs, recv_pid);
				if (procset_isempty(r->procs)) {
					RemoveFromList(active_requests, (void *)r);
					r->completed(r->events);
					procset_free(r->procs);
					free(r);
				}
			}
			
			break;
		}
		
		free(reply_buf);
		
		/*
		 * remove from receiving procsets
		 */
		procset_remove_proc(receiving_procs, recv_pid);
	}
}

#define TEST

#ifdef TEST
int completed;

void 
send_complete(Hash *h)
{
	HashEntry *	he;
	dbg_event *	e;
	
	for (HashSet(h); (he = HashGet(h)) != NULL; ) {
		e = (dbg_event *)he->h_data;
		printf("hash[0x%0x] = <%d,%s>\n", he->h_hval, e->event, procset_to_str(e->procs));
	}
	
	printf("send completed\n");
	completed++;
}

void
wait_for_server(void)
{
	completed = 0;
	
	while (!completed) {
		progress_commands();
		usleep(10000);
	}
}
#endif

void 
client(int task_id)
{
	int	i;
#ifdef TEST
	procset *p;
#endif

	num_servers = my_task_id = task_id;
	
	send_bufs = (char **)malloc(sizeof(char *) * num_servers);
	send_requests = (MPI_Request *) malloc(sizeof(MPI_Request) * num_servers);
	pids = (int *)malloc(sizeof(int) * num_servers);
	stats = (MPI_Status *)malloc(sizeof(MPI_Status) * num_servers);

	for (i = 0; i < num_servers; i++)
		send_requests[i] = MPI_REQUEST_NULL;

	sending_procs = procset_new(num_servers);
	receiving_procs = procset_new(num_servers);
	active_requests = NewList();
	
#ifdef TEST
	p = procset_new(num_servers);
	for (i = 0; i < num_servers; i++)
		procset_add_proc(p, i);
	
	send_command(p, "hello", send_complete);
	wait_for_server();
	send_command(p, "SLB", send_complete);
	wait_for_server();
	send_command(p, "QUI", send_complete);
	wait_for_server();
	
#endif
}
