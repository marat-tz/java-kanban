import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TaskManager {
    private final Map<Integer, Task> idToTask = new HashMap<>(); // убрал static, добавил final, поменял тип
    private final Map<Integer, Subtask> idToSubtask = new HashMap<>();
    private final Map<Integer, Epic> idToEpic = new HashMap<>();
    private int taskId = 1; // убрал static

    public Task addNewTask(Task newTask) {
        int newId = generateNewId();
        newTask.setId(newId);

        if (newTask.getClass() == Task.class) {
            idToTask.put(newTask.getId(), newTask);
            System.out.print("Added task: ");

        } else if (newTask.getClass() == Subtask.class) {
            Subtask subtask;
            try {
                subtask = (Subtask) newTask;
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
                return null;
            }
            if (subtask.getCurrentEpicId() != null) {
                ((Epic) getTask(subtask.getCurrentEpicId())).addSubtask(subtask.getId());
                refreshEpicStatus(subtask.getCurrentEpicId());
                idToSubtask.put(subtask.getId(), subtask);
                System.out.print("Added subtask: ");
            } else {
                System.out.print("Subtask must contain Epic ");
                return null;
            }

        } else if (newTask.getClass() == Epic.class) {
            Epic epic;
            try {
                epic = (Epic) newTask;
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
                return null;
            }
            idToEpic.put(epic.getId(), epic);
            System.out.print("Added epic: ");
        }
        return newTask;
    }

    public Task updateTask(Task updatedTask) {
        Integer taskId = updatedTask.getId();

        if (idToTask.containsKey(taskId) && updatedTask.getClass() == Task.class) {
            idToTask.put(taskId, updatedTask);
            System.out.print("Updated task: ");

        } else if (idToSubtask.containsKey(taskId) && updatedTask.getClass() == Subtask.class) {
            Subtask subtaskMap = idToSubtask.get(updatedTask.getId());
            Subtask subtask;
            try {
                subtask = (Subtask) updatedTask;
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
                return null;
            }
            if (subtask.getCurrentEpicId() == null) {
                subtask.setCurrentEpic(subtaskMap.getCurrentEpicId());
            }
            ((Epic) getTask(subtask.getCurrentEpicId())).addSubtask(subtask.getId());
            refreshEpicStatus(subtask.getCurrentEpicId());
            idToSubtask.put(taskId, subtask);
            System.out.print("Updated subtask: ");

        } else if (idToEpic.containsKey(taskId) && updatedTask.getClass() == Epic.class) {
            Epic epic;
            try {
                epic = (Epic) updatedTask;
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
                return null;
            }
            idToEpic.put(taskId, epic);
            System.out.print("Updated epic: ");

        } else {
            System.out.print("Задачи с таким id нет ");
            return null;
        }

        return updatedTask;
    }

    public Task deleteTask(Integer taskId) {
        Task removedTask;

        if (idToTask.containsKey(taskId)) {
            removedTask = idToTask.get(taskId);
            idToTask.remove(taskId);

        } else if (idToSubtask.containsKey(taskId)) {
            Subtask subtask;
            try {
                subtask = idToSubtask.get(taskId);
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
                return null;
            }
            ((Epic) getTask(subtask.getCurrentEpicId())).removeSubtask(taskId);
            refreshEpicStatus(subtask.getCurrentEpicId());
            removedTask = idToSubtask.get(taskId);
            idToSubtask.remove(taskId);

        } else if (idToEpic.containsKey(taskId)) {
            removedTask = idToEpic.get(taskId);
            idToEpic.remove(taskId);

        } else {
            System.out.print("Указанный id отсутствует ");
            return null;
        }

        System.out.println("Removed task: " + removedTask);
        return removedTask;
    }

    public void deleteAllTasks() {
        int tasksSum = 0;
        if (!idToTask.isEmpty()) {
            tasksSum = idToTask.size();
            idToTask.clear();
        }
        System.out.println("Removed " + tasksSum + " tasks");
    }

    public void deleteAllSubtasks() { // теперь затрагиваются только те эпики, в которых были сабтаски
        int tasksSum = 0;
        if (!idToSubtask.isEmpty()) {
            Epic epic;
            for(Subtask subtask : idToSubtask.values()) {
                epic = (Epic) getTask(subtask.getCurrentEpicId());
                epic.clearSubtasks();
                refreshEpicStatus(epic.getId());
            }
            tasksSum = idToSubtask.size();
            idToSubtask.clear();
        }
        System.out.println("Removed " + tasksSum + " subtasks");
    }

    public void deleteAllEpic() { // исправлено удаление сабтасков
        int tasksSum = 0;
        if (!idToEpic.isEmpty()) {
            for (Epic epic : idToEpic.values()) {
                for (Integer subtaskId : epic.getEpicSubtasksId()) {
                    idToSubtask.remove(subtaskId);
                }
            }
            tasksSum = idToEpic.size();
            idToEpic.clear();
        }
        System.out.println("Removed " + tasksSum + " epics");
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!idToTask.isEmpty()) {
            tasks.addAll(idToTask.values());
            return tasks;
        }
        System.out.print("Tasks list is empty ");
        return null;
    }

    public ArrayList<Task> getAllSubtasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!idToSubtask.isEmpty()) {
            tasks.addAll(idToSubtask.values());
            return tasks;
        }
        System.out.print("Subtasks list is empty ");
        return null;
    }

    public ArrayList<Task> getAllEpic() {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!idToEpic.isEmpty()) {
            tasks.addAll(idToEpic.values());
            return tasks;
        }
        System.out.print("Epic tasks list is empty ");
        return null;
    }

    public Task getTask(Integer taskId) {
        Task requestedTask;
        if (idToTask.containsKey(taskId)) {
            requestedTask = idToTask.get(taskId);

        } else if (idToSubtask.containsKey(taskId)) {
            requestedTask = idToSubtask.get(taskId);

        } else if (idToEpic.containsKey(taskId)) {
            requestedTask = idToEpic.get(taskId);

        } else {
            System.out.print("Указанный id отсутствует");
            return null;
        }

        System.out.print("Requested task: ");
        return requestedTask;
    }

    public ArrayList<Subtask> getEpicSubtasks(Integer epicId) {
        Epic epicInMap = idToEpic.get(epicId);
        ArrayList<Subtask> subtasks = new ArrayList<>();
        if (!epicInMap.getEpicSubtasksId().isEmpty()) {
            for(Integer subtaskId : epicInMap.getEpicSubtasksId()) {
                subtasks.add(idToSubtask.get(subtaskId));
            }
            System.out.print("Epic " + epicInMap.getName() + " contains: ");
            return subtasks;
        }
        System.out.print("Epic not contains subtasks ");
        return null;
    }

    public void deleteEpicSubtasks(Integer epicId) {
        Epic epicInMap = idToEpic.get(epicId);
        if (!getEpicSubtasks(epicId).isEmpty()) {
            epicInMap.clearSubtasks();
            refreshEpicStatus(epicId);
            System.out.println("Removed all subtasks from " + epicInMap.getName());
        } else {
            System.out.println("Epic not contains subtasks");
        }
    }

    public void refreshEpicStatus(Integer epicId) { // вынести в менеджер
        int countNew = 0;
        int countDone = 0;

        if (getEpicSubtasks(epicId).isEmpty()) {
            getTask(epicId).setStatus(TaskStatus.NEW);
        } else {
            for (Subtask subtask : getEpicSubtasks(epicId)) { // тут может кинуть null
                if (subtask.getStatus().equals(TaskStatus.NEW)) {
                    countNew++;
                } else if (subtask.getStatus().equals(TaskStatus.DONE)){
                    countDone++;
                }
            }

            if (countNew == getEpicSubtasks(epicId).size()) {
                getTask(epicId).setStatus(TaskStatus.NEW);
            } else if (countDone == getEpicSubtasks(epicId).size()) {
                getTask(epicId).setStatus(TaskStatus.DONE);
            } else {
                getTask(epicId).setStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }

    private int generateNewId() {
        return taskId++;
    }
}
