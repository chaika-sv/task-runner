// Загружаем список задач
async function loadTasks() {
    try {
        const response = await fetch('/api/jobs');

        const text = await response.text();
        const tasks = JSON.parse(text);



        const list = document.getElementById('taskList');
        list.innerHTML = '';

        tasks.forEach(task => {
            const li = document.createElement('li');

            // Заголовок (имя задачи)
            const nameDiv = document.createElement('div');
            nameDiv.textContent = task.name;
            nameDiv.style.fontWeight = 'bold';

            // Cron под названием (если есть)
            const cronDiv = document.createElement('div');
            cronDiv.textContent = task.cron ? `cron: ${task.cron}` : 'no schedule';
            cronDiv.style.fontSize = '12px';
            cronDiv.style.color = '#555';

            li.appendChild(nameDiv);
            li.appendChild(cronDiv);

            list.appendChild(li);
        });

    } catch (e) {
        console.error('Failed to load tasks:', e);
    }
}

// Стартуем на загрузку страницы
window.onload = () => {
    loadTasks();
};
