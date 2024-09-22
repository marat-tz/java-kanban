package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    // Исключения вида IOException нужно отлавливать внутри метода save и выкидывать собственное
    // непроверяемое исключение ManagerSaveException.
    // Благодаря этому можно не менять сигнатуру методов интерфейса менеджера.

    // Создайте метод save без параметров — он будет сохранять текущее состояние менеджера в указанный файл.
    // Он должен сохранять все задачи, подзадачи и эпики.
    private void save() {
        try (Writer fileWriter = new FileWriter(file)) {
            fileWriter.write("ID,TYPE,NAME,STATUS,DESCRIPTION,EPIC\n");
            for (int i = 0; i < getLastId(); i++) {
                fileWriter.write(getTask(i).toString());
            }
        } catch (IOException ex) {
            //throw new ManagerSaveException();
        }
    }

    private Task fromString(String value) {
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

        return null;
    }

    @Override
    public Task addNewTask(Task newTask) {
        Task task = super.addNewTask(newTask);
        save();
        return task;
    }

    @Override
    public Epic addNewTask(Epic newEpic) {
        Epic epic = super.addNewTask(newEpic);
        save();
        return epic;
    }

    @Override
    public Subtask addNewTask(Subtask newSubtask) {
        Subtask subtask = super.addNewTask(newSubtask);
        save();
        return subtask;
    }

    @Override
    public Task updateTask(Task updatedTask) {
        Task task = super.updateTask(updatedTask);
        save();
        return task;
    }

    @Override
    public Subtask updateTask(Subtask subtaskUpdate) {
        Subtask subtask = super.updateTask(subtaskUpdate);
        save();
        return subtask;
    }

    @Override
    public Epic updateTask(Epic epicUpdate) {
        Epic epic = super.updateTask(epicUpdate);
        save();
        return epic;
    }

    @Override
    public Task deleteTask(Integer taskId) {
        Task task =  super.deleteTask(taskId);
        save();
        return task;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();
        save();
    }

    @Override
    public void deleteEpicSubtasks(Integer epicId) {
        super.deleteEpicSubtasks(epicId);
        save();
    }
}
