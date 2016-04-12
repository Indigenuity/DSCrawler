update taskset ts
join taskset_task tst on ts.tasksetid = tst.tasksetid
join task t on tst.taskid = t.taskid
join task_subtask sub on t.taskid = sub.supertaskid
join task t2 on sub.subtaskid = t2.taskid
join task_contextitems ci on t2.taskid = ci.task_taskid
set ci.contextitems = Replace(ci.contextitems, 'cs:e=hma', '')
t2.workstatus = 'MORE_WORK'
t.workstatus = 'DO_WORK'
where ts.tasksetid = 1
and ci.contextitems like '%cs:e=hma%'
group by t2.workType, t2.workstatus
order by t2.workstatus