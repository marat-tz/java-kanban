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
import java.util.HashMap;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {

    protected Map<Integer, Task> idTask = new HashMap<>();
    protected Map<Integer, Subtask> idSubtask = new HashMap<>();
    protected Map<Integer, Epic> idEpic = new HashMap<>();
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public FileBackedTaskManager(File file, Map<Integer, Task> tasks,
                                 Map<Integer, Epic> epics,
                                 Map<Integer, Subtask> subtasks) {
        this.file = file;
        this.idTask = tasks;
        this.idEpic = epics;
        this.idSubtask = subtasks;

    }

    private void save() throws ManagerSaveException {
        try (Writer fileWriter = new FileWriter(file)) {

            if (!getAllTasks().isEmpty() || !getAllSubtasks().isEmpty() || !getAllEpic().isEmpty()) {
                fileWriter.write("ID,TYPE,NAME,STATUS,DESCRIPTION,EPIC\n");

                for (Task task : getAllTasks()) {
                    fileWriter.write(task.toString() + "\n");
                }

                for (Task epic : getAllEpic()) {
                    fileWriter.write(epic.toString() + "\n");
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

        switch (temp[3]) {
            case "NEW":
                status = TaskStatus.NEW;
                break;
            case "IN_PROGRESS":
                status = TaskStatus.IN_PROGRESS;
                break;
            case "DONE":
                status = TaskStatus.DONE;
                break;
            default:
                status = TaskStatus.NEW;
        }

        switch (temp[1]) {
            case "TASK":
                return new Task(id, name, description, status);
            case "SUBTASK":
                int epicId = Integer.parseInt(temp[5]);
                return new Subtask(id, name, description, epicId, status);
            case "EPIC":
                return new Epic(id, name, description, status);
            default:
                return null;
        }
    }

    public FileBackedTaskManager loadFromFile(File file) throws ManagerLoadException {

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            int lastId = 0;
            Task task = null;

            while (br.ready()) {
                String currLine = br.readLine();
                if (!currLine.startsWith("ID")) {
                    task = fromString(currLine);
                }

                if (task != null) {
                    lastId = Math.max(lastId, task.getId() + taskId);

                    switch (task.getType()) {
                        case TASK:
                            idTask.put(task.getId() + taskId, task); // прибавляем taskId на случай если уже есть задачи
                            break;
                        case SUBTASK:
                            idSubtask.put(task.getId() + taskId, (Subtask) task);
                            break;
                        case EPIC:
                            idEpic.put(task.getId() + taskId, (Epic) task);
                    }
                }
            }

            taskId = lastId;

            for (Subtask sub : idSubtask.values()) {
                Epic epic = idEpic.get(sub.getEpicId());
                epic.addSubtask(sub);
                refreshEpicStatus(epic.getId());
            }

        } catch (IOException ex) {
            throw new ManagerLoadException(ex.getMessage());
        }

        return new FileBackedTaskManager(file, idTask, idEpic, idSubtask);
    }

    @Override
    public Task addNewTask(Task newTask) {
        Task task = super.addNewTask(newTask);

        try {
            save();
        } catch (ManagerSaveException ex) {
            ex.printStackTrace();
        }

        return task;
    }

    @Override
    public Epic addNewTask(Epic newEpic) {
        Epic epic = super.addNewTask(newEpic);

        try {
            save();
        } catch (ManagerSaveException ex) {
            ex.printStackTrace();
        }

        return epic;
    }

    @Override
    public Subtask addNewTask(Subtask newSubtask) {
        Subtask subtask = super.addNewTask(newSubtask);

        try {
            save();
        } catch (ManagerSaveException ex) {
            ex.printStackTrace();
        }

        return subtask;
    }

    @Override
    public Task updateTask(Task updatedTask) {
        Task task = super.updateTask(updatedTask);

        try {
            save();
        } catch (ManagerSaveException ex) {
            ex.printStackTrace();
        }

        return task;
    }

    @Override
    public Subtask updateTask(Subtask subtaskUpdate) {
        Subtask subtask = super.updateTask(subtaskUpdate);

        try {
            save();
        } catch (ManagerSaveException ex) {
            ex.printStackTrace();
        }

        return subtask;
    }

    @Override
    public Epic updateTask(Epic epicUpdate) {
        Epic epic = super.updateTask(epicUpdate);

        try {
            save();
        } catch (ManagerSaveException ex) {
            ex.printStackTrace();
        }

        return epic;
    }

    @Override
    public Task deleteTask(Integer taskId) {
        Task task =  super.deleteTask(taskId);

        try {
            save();
        } catch (ManagerSaveException ex) {
            ex.printStackTrace();
        }

        return task;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();

        try {
            save();
        } catch (ManagerSaveException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();

        try {
            save();
        } catch (ManagerSaveException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();

        try {
            save();
        } catch (ManagerSaveException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void deleteEpicSubtasks(Integer epicId) {
        super.deleteEpicSubtasks(epicId);

        try {
            save();
        } catch (ManagerSaveException ex) {
            ex.printStackTrace();
        }
    }
}
