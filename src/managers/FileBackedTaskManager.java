package managers;

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

    private File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    // будет сохранять текущее состояние менеджера в указанный файл.
    // Он должен сохранять все задачи, подзадачи и эпики.
    private void save() throws ManagerSaveException {
        try (Writer fileWriter = new FileWriter(file)) {
            fileWriter.write("ID,TYPE,NAME,STATUS,DESCRIPTION,EPIC\n");
            for (int i = 0; i < getLastId(); i++) {
                fileWriter.write(getTask(i).toString());
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
            case "TaskStatus.NEW":
                status = TaskStatus.NEW;
                break;
            case "TaskStatus.IN_PROGRESS":
                status = TaskStatus.IN_PROGRESS;
                break;
            case "TaskStatus.DONE":
                status = TaskStatus.DONE;
                break;
            default:
                status = TaskStatus.NEW;
        }

        switch (temp[1]) {
            case "TaskType.TASK":
                return new Task(id, name, description, status);
            case "TaskType.SUBTASK":
                int epicId = Integer.parseInt(temp[5]);
                return new Subtask(id, name, description, epicId, status);
            case "TaskType.EPIC":
                return new Epic(id, name, description, status);
            default:
                return null;
        }
    }

    // будет восстанавливать данные менеджера из файла при запуске программы
    public static FileBackedTaskManager loadFromFile(File file) {
        Map<Integer, Task> tempTask = new HashMap<>();
        Map<Integer, Subtask> tempSub = new HashMap<>();
        Map<Integer, Epic> tempEpic = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            while (br.ready()) {
                Task task = fromString(br.readLine());
                switch (task.getType()) {
                    case TASK:
                        tempTask.put(task.getId(), task);
                        break;
                    case SUBTASK:
                        tempSub.put(task.getId(), (Subtask) task);
                        break;
                    case EPIC:
                        tempEpic.put(task.getId(), (Epic) task);
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return new FileBackedTaskManager(file);
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
