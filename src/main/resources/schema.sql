create table schedule (
                          id bigint generated always as identity primary key,
                          name varchar(255) not null,
                          task_name varchar(255) not null,
                          cron varchar(255) not null
);

insert into schedule(name, task_name, cron) values
('Test 17 schedule', 'test17', '0 55 16 * * *'),
('Every 2 min', 'test2min', '0 */2 * * * *');
