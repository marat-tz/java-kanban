import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TaskManager {
    private static Map<Integer, Task> idToTask = new HashMap<>();
    private static Map<Integer, Task> idToSubtask = new HashMap<>();
    private static Map<Integer, Task> idToEpic = new HashMap<>();
    private static int taskId = 1;

    public Task addNewTask(Task newTask) {
        int newId = generateNewId();
        newTask.setId(newId);

        if (newTask.getClass().getName().equals("tasks.Task")) {
            idToTask.put(newTask.getId(), newTask);
            System.out.print("Added task: ");

        } else if (newTask.getClass().getName().equals("tasks.Subtask")) {
            Subtask subtask;
            try {
                subtask = (Subtask) newTask;
            } catch (Exception exception) {
                throw exception;
            }
            subtask.getCurrentEpic().addSubtask(newTask.getId(), newTask);
            subtask.getCurrentEpic().refreshEpicStatus();
            idToSubtask.put(newTask.getId(), newTask);
            System.out.print("Added subtask: ");

        } else {
            idToEpic.put(newTask.getId(), newTask);
            System.out.print("Added epic: ");
        }
        return newTask;
    }

    public Task updateTask(Task updatedTask) {
        Integer taskId = updatedTask.getId();

        if (idToTask.containsKey(taskId) && updatedTask.getClass().getName().equals("tasks.Task")) {
            idToTask.put(taskId, updatedTask);
            System.out.print("Updated task: ");
            return updatedTask;

        } else if (idToSubtask.containsKey(taskId) && updatedTask.getClass().getName().equals("tasks.Subtask")) {
            Subtask subtask = (Subtask) idToSubtask.get(updatedTask.getId());
            Subtask subtaskTemp;

            try {
                subtaskTemp = (Subtask) updatedTask;
            } catch (Exception exception) {
                System.out.println(exception);
                return null;
            }

            if (subtaskTemp.getCurrentEpic() == null) {
                subtaskTemp.setCurrentEpic(subtask.getCurrentEpic());
            }

            subtask.getCurrentEpic().addSubtask(taskId, subtaskTemp);
            subtask.getCurrentEpic().refreshEpicStatus();
            idToSubtask.put(taskId, subtaskTemp);
            System.out.print("Updated subtask: ");
            return subtaskTemp;

        } else if (idToEpic.containsKey(taskId) && updatedTask.getClass().getName().equals("tasks.Epic")) {
            idToEpic.put(taskId, updatedTask);
            System.out.print("Updated epic: ");
            return updatedTask;

        } else {
            System.out.print("Задачи с таким id нет ");
            return null;
        }
    }

    public Task deleteTask(Task deleteTask) {
        Integer taskId = deleteTask.getId();
        Task removedTask;
        if (idToTask.containsKey(taskId) && deleteTask.getClass().getName().equals("tasks.Task")) {
            removedTask = idToTask.get(taskId);
            idToTask.remove(taskId);

        } else if (idToSubtask.containsKey(taskId) && deleteTask.getClass().getName().equals("tasks.Subtask")) {
            Subtask subtask;

            try {
                subtask = (Subtask) deleteTask;
            } catch (Exception exception) {
                throw exception;
            }

            subtask.getCurrentEpic().removeSubtask(taskId);
            subtask.getCurrentEpic().refreshEpicStatus();
            removedTask = idToSubtask.get(taskId);
            idToSubtask.remove(taskId);

        } else if (idToEpic.containsKey(taskId) && deleteTask.getClass().getName().equals("tasks.Epic")) {
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

    public void deleteAllSubtasks() {
        int tasksSum = 0;
        if (!idToSubtask.isEmpty()) {
            tasksSum = idToSubtask.size();
            idToSubtask.clear();
            for (Task epicTask : idToEpic.values()) {
                try {
                    Epic epic = (Epic) epicTask;
                    epic.clearSubtasks();
                    epic.refreshEpicStatus();
                } catch (Exception exception) {
                    throw exception;
                }
            }
        }

        System.out.println("Removed " + tasksSum + " subtasks");
    }

    public void deleteAllEpic() {
        int tasksSum = 0;
        if (!idToEpic.isEmpty()) {
            tasksSum += idToEpic.size();
            idToEpic.clear();
        }

        System.out.println("Removed " + tasksSum + " epics");
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!idToTask.isEmpty()) {
            for (Task task : idToTask.values()) {
                tasks.add(task);
            }
            return tasks;
        }
        System.out.print("Tasks list is empty ");
        return null;
    }

    public ArrayList<Task> getAllSubtasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!idToSubtask.isEmpty()) {
            for (Task task : idToSubtask.values()) {
                tasks.add(task);
            }
            return tasks;
        }
        System.out.print("Subtasks list is empty ");
        return null;
    }

    public ArrayList<Task> getAllEpic() {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!idToEpic.isEmpty()) {
            for (Task task : idToEpic.values()) {
                tasks.add(task);
            }
            return tasks;
        }
        System.out.print("Epic tasks list is empty ");
        return null;
    }

    public Task getTask(Task task) {
        Task requestedTask;
        if (idToTask.containsKey(task.getId())) {
            requestedTask = idToTask.get(task.getId());

        } else if (idToSubtask.containsKey(task.getId())) {
            requestedTask = idToSubtask.get(task.getId());

        } else if (idToEpic.containsKey(task.getId())){
            requestedTask = idToEpic.get(task.getId());

        } else {
            System.out.print("Указанный id отсутствует");
            return null;
        }

        System.out.print("Requested task: ");
        return requestedTask;
    }

    public ArrayList<Task> getEpicSubtasks(Task epicTask) {
        Epic epic;

        try {
            epic = (Epic) epicTask;
        } catch (Exception exception) {
            throw exception;
        }

        ArrayList<Task> subtasks = new ArrayList<>();

        if (!epic.getSubtasks().isEmpty()) {
            for (Task subtask : epic.getSubtasks().values()) {
                subtasks.add(subtask);
            }
            System.out.print("Epic " + epic.getName() + " contains: ");
            return subtasks;
        }

        System.out.print("Epic not contains subtasks ");
        return null;
    }

    public void deleteEpicSubtasks(Task epicTask) {
        Epic epic;
        try {
            epic = (Epic) epicTask;
        } catch (Exception exception) {
            throw exception;
        }

        if (!epic.getSubtasks().isEmpty()) {
            epic.clearSubtasks();
            epic.refreshEpicStatus();
            System.out.println("Removed all subtasks from " + epic.getName());
        } else {
            System.out.println("Epic not contains subtasks");
        }
    }

    private int generateNewId() {
        return taskId++;
    }

}