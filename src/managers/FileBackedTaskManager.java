package managers;

import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;
    private static final Logger logger = Logger.getAnonymousLogger();

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public FileBackedTaskManager(File file, Map<Integer, Task> tasks,
                                 Map<Integer, Epic> epics,
                                 Map<Integer, Subtask> subtasks,
                                 int maxId) {
        this.file = file;
        this.idTask = tasks;
        this.idEpic = epics;
        this.idSubtask = subtasks;
        this.taskId = maxId;
    }

    private void save() throws ManagerSaveException {
        try (Writer fileWriter = new FileWriter(file)) {

            if (!getAllTasks().isEmpty() || !getAllSubtasks().isEmpty() || !getAllEpic().isEmpty()) {
                fileWriter.write("ID,TYPE,NAME,STATUS,DESCRIPTION,DURATION,START_TIME,EPIC\n");

                for (Task task : getAllTasks()) {
                    fileWriter.write(task.toString() + ",," + "\n");
                }

                for (Task epic : getAllEpic()) {
                    fileWriter.write(epic.toString() + ",," + "\n");
                }

                for (Task subtask : getAllSubtasks()) {
                    fileWriter.write(subtask.toString() + "\n");
                }
            }

        } catch (IOException ex) {
            throw new ManagerSaveException(ex.getMessage());
        }
    }

    private static Task fromString(String value) {
        String[] temp = value.split(",");
        int id = Integer.parseInt(temp[0]);
        String name = temp[2];
        String description = temp[4];
        TaskStatus status;
        Duration duration = Duration.ofMinutes(Integer.parseInt(temp[5]));
        LocalDateTime startTime = LocalDateTime.parse(temp[6]);

        status = switch (temp[3]) {
            case "IN_PROGRESS" -> TaskStatus.IN_PROGRESS;
            case "DONE" -> TaskStatus.DONE;
            default -> TaskStatus.NEW;
        };

        switch (temp[1]) {
            case "TASK":
                return new Task(id, name, description, status, duration, startTime);
            case "SUBTASK":
                int epicId = Integer.parseInt(temp[7]);
                return new Subtask(id, name, description, status, duration, startTime, epicId);
            case "EPIC":
                return new Epic(id, name, description, status, duration, startTime);
            default:
                return null;
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerLoadException {

        Map<Integer, Task> tasks = new HashMap<>();
        Map<Integer, Subtask> subtasks = new HashMap<>();
        Map<Integer, Epic> epics = new HashMap<>();
        int maxId = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            Task task = null;
            while (br.ready()) {
                String currLine = br.readLine();
                if (!currLine.startsWith("ID")) {
                    task = fromString(currLine);
                }

                if (task != null) {
                    maxId = Math.max(maxId, task.getId());

                    switch (task.getType()) {
                        case TASK -> tasks.put(task.getId(), task);
                        case SUBTASK -> subtasks.put(task.getId(), (Subtask) task);
                        case EPIC -> epics.put(task.getId(), (Epic) task);
                    }
                }
            }

            subtasks.values().forEach(subtask -> {
                Epic epic = epics.get(subtask.getEpicId());
                epic.addSubtask(subtask);
            });

        } catch (IOException ex) {
            throw new ManagerLoadException(ex.getMessage());
        }

        return new FileBackedTaskManager(file, tasks, epics, subtasks, maxId);
    }

    @Override
    public Task addNewTask(Task newTask) {
        Task task = super.addNewTask(newTask);

        try {
            save();
        } catch (ManagerSaveException ex) {
            logger.log(Level.SEVERE, "error while adding task", ex);
        }

        return task;
    }

    @Override
    public Epic addNewTask(Epic newEpic) {
        Epic epic = super.addNewTask(newEpic);

        try {
            save();
        } catch (ManagerSaveException ex) {
            logger.log(Level.SEVERE, "error while adding epic", ex);
        }

        return epic;
    }

    @Override
    public Subtask addNewTask(Subtask newSubtask) {
        Subtask subtask = super.addNewTask(newSubtask);

        try {
            save();
        } catch (ManagerSaveException ex) {
            logger.log(Level.SEVERE, "error while adding subtask", ex);
        }

        return subtask;
    }

    @Override
    public Task updateTask(Task updatedTask) {
        Task task = super.updateTask(updatedTask);

        try {
            save();
        } catch (ManagerSaveException ex) {
            logger.log(Level.SEVERE, "error while updating task", ex);
        }

        return task;
    }

    @Override
    public Subtask updateTask(Subtask subtaskUpdate) {
        Subtask subtask = super.updateTask(subtaskUpdate);

        try {
            save();
        } catch (ManagerSaveException ex) {
            logger.log(Level.SEVERE, "error while updating subtask", ex);
        }

        return subtask;
    }

    @Override
    public Epic updateTask(Epic epicUpdate) {
        Epic epic = super.updateTask(epicUpdate);

        try {
            save();
        } catch (ManagerSaveException ex) {
            logger.log(Level.SEVERE, "error while updating epic", ex);
        }

        return epic;
    }

    @Override
    public Task deleteTask(Integer taskId) {
        Task task =  super.deleteTask(taskId);

        try {
            save();
        } catch (ManagerSaveException ex) {
            logger.log(Level.SEVERE, "error while removing task", ex);
        }

        return task;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();

        try {
            save();
        } catch (ManagerSaveException ex) {
            logger.log(Level.SEVERE, "error while removing all tasks", ex);
        }
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();

        try {
            save();
        } catch (ManagerSaveException ex) {
            logger.log(Level.SEVERE, "error while removing all subtasks", ex);
        }
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();

        try {
            save();
        } catch (ManagerSaveException ex) {
            logger.log(Level.SEVERE, "error while removing all epics", ex);
        }
    }

    @Override
    public void deleteEpicSubtasks(Integer epicId) {
        super.deleteEpicSubtasks(epicId);

        try {
            save();
        } catch (ManagerSaveException ex) {
            logger.log(Level.SEVERE, "error while removing all epic subtasks", ex);
        }
    }
}
