update taskset ts
join taskset_task tst on ts.tasksetid = tst.tasksetid
join task t on t.taskid = tst.taskid
join task_subtask sub on t.taskid = sub.supertaskId
join task t2 on sub.subtaskid = t2.taskid
set t.workStatus = 'DO_WORK',
t2.workStatus = 'DO_WORK'
where ts.tasksetid = 1
and t2.worktype = 'INVENTORY_COUNT'
and t2.workStatus = 'NEEDS_REVIEW'