Simple CRM sistema на SpringBoot
1. Users - существуют 3 роли ADMIN, USER(DEFAULT) и CONTROLLER;
2. Tasks - задачи для выполнения, у одной задачи несколько исполнителей;
3. Задача имеет статус: new, performed, completed, время исполнения - dedline и info;
4. У одного user может быть несколько задач;
5. Admin может ставить задачу, менять ее статус, переносить dedline;
6. Admin может создавать/удалять/менять user;
7. User может изменить статус задачи;