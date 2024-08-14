import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryTaskManager {
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
            return newTask;
        } else {
            System.out.println("Received class is not Task");
            return null;
        }
    }

    public Epic addNewTask(Epic newEpic) {
        int newId = generateNewId();
        newEpic.setId(newId);

        idToEpic.put(newEpic.getId(), newEpic);
        System.out.print("Added epic: ");
        return newEpic;
    }

    public Subtask addNewTask(Subtask newSubtask) {
        int newId = generateNewId();
        Integer subtaskEpicId = newSubtask.getEpicId();
        newSubtask.setId(newId);

        if (subtaskEpicId != null && subtaskEpicId > 0) {
            getEpic(newSubtask.getEpicId()).addSubtask(newSubtask.getId());
            idToSubtask.put(newSubtask.getId(), newSubtask);
            refreshEpicStatus(newSubtask.getEpicId());
            System.out.print("Added subtask: ");
            return newSubtask;
        } else {
            System.out.print("Subtask must contain Epic ");
            return null;
        }
    }

    public Task updateTask(Task updatedTask) {
        Integer taskId = updatedTask.getId();

        if (idToTask.containsKey(taskId) && updatedTask.getClass() == Task.class) {
            if (updatedTask.getStatus() == null) {
                updatedTask.setStatus(getTask(taskId).getStatus());
            }
            idToTask.put(taskId, updatedTask);
            System.out.print("Updated task: ");
            return updatedTask;
        } else {
            System.out.print("Задачи с таким id нет ");
            return null;
        }
    }

    public Subtask updateTask(Subtask subtaskUpdate) {
        Integer subtaskId = subtaskUpdate.getId();

        if (idToSubtask.containsKey(subtaskId)) {
            Subtask subtaskMap = idToSubtask.get(subtaskId);

            if (subtaskUpdate.getStatus() == null) {
                subtaskUpdate.setStatus(getTask(subtaskId).getStatus());
            }
            if (subtaskUpdate.getEpicId() == null) {
                subtaskUpdate.setEpic(subtaskMap.getEpicId());
                idToSubtask.put(subtaskId, subtaskUpdate);
                refreshEpicStatus(subtaskUpdate.getEpicId());
                System.out.print("Updated subtask: ");

            } else if (!subtaskUpdate.getEpicId().equals(idToSubtask.get(subtaskId).getEpicId())) { // если эпик указан другой, то нужно удалить сабтаск у старого
                getEpic(idToSubtask.get(subtaskId).getEpicId()).removeSubtask(subtaskId);
                refreshEpicStatus(idToSubtask.get(subtaskId).getEpicId());
                idToSubtask.put(subtaskId, subtaskUpdate);
                getEpic(subtaskUpdate.getEpicId()).addSubtask(subtaskId);
                refreshEpicStatus(subtaskUpdate.getEpicId());
                System.out.print("Updated subtask: ");
            }

        } else {
            System.out.print("Задачи с таким id нет ");
            return null;
        }

        return subtaskUpdate;
    }

    public Epic updateTask(Epic epicUpdate) {
        Integer epicId = epicUpdate.getId();

        if (idToEpic.containsKey(epicId)) {
            if (epicUpdate.getStatus() == null) {
                epicUpdate.setStatus(getTask(epicId).getStatus());
            }
            epicUpdate.cloneSubtask(idToEpic.get(epicId).getEpicSubtasksId());
            idToEpic.put(epicId, epicUpdate);
            System.out.print("Updated epic: ");
            return epicUpdate;
        } else {
            System.out.print("Задачи с таким id нет ");
            return null;
        }
    }

    public Task deleteTask(Integer taskId) {
        Task removedTask;

        if (idToTask.containsKey(taskId)) {
            removedTask = idToTask.get(taskId);
            idToTask.remove(taskId);

        } else if (idToSubtask.containsKey(taskId)) {
            Subtask subtask = idToSubtask.get(taskId);
            getEpic(subtask.getEpicId()).removeSubtask(taskId);
            refreshEpicStatus(subtask.getEpicId());
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

    public void deleteAllSubtasks() {
        int tasksSum = 0;
        if (!idToSubtask.isEmpty()) {
            Epic epic;
            for(Subtask subtask : idToSubtask.values()) {
                epic = getEpic(subtask.getEpicId());
                epic.clearSubtasks();
                refreshEpicStatus(epic.getId());
            }
            tasksSum = idToSubtask.size();
            idToSubtask.clear();
        }
        System.out.println("Removed " + tasksSum + " subtasks");
    }

    public void deleteAllEpic() {
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
        if (!idToTask.isEmpty()) {
            return new ArrayList<>(idToTask.values());
        }
        System.out.print("Tasks list is empty: ");
        return null;
    }

    public ArrayList<Task> getAllSubtasks() {
        if (!idToSubtask.isEmpty()) {
            return new ArrayList<>(idToSubtask.values());
        }
        System.out.print("Subtasks list is empty: ");
        return null;
    }

    public ArrayList<Task> getAllEpic() {
        if (!idToEpic.isEmpty()) {
            return new ArrayList<>(idToEpic.values());
        }
        System.out.print("Epic tasks list is empty: ");
        return null;
    }

    public Task getTask(Integer taskId) {
        if (idToTask.containsKey(taskId)) {
            return idToTask.get(taskId);
        } else {
            System.out.print("Указанный id отсутствует");
            return null;
        }
    }

    public Epic getEpic(Integer epicId) {
        if (idToEpic.containsKey(epicId)) {
            return idToEpic.get(epicId);
        } else {
            System.out.print("Указанный Epic-id отсутствует");
            return null;
        }
    }

    public Subtask getSubtask(Integer subtaskId) {
        if (idToSubtask.containsKey(subtaskId)) {
            return idToSubtask.get(subtaskId);
        } else {
            System.out.print("Указанный Subtask-id отсутствует");
            return null;
        }
    }

    public ArrayList<Subtask> getEpicSubtasks(Integer epicId) {
        Epic epicInMap;
        ArrayList<Subtask> subtasks = new ArrayList<>();

        if (!idToEpic.containsKey(epicId)) {
            System.out.print("Not Epic with this id: ");
            return null;
        } else {
            epicInMap = idToEpic.get(epicId);
        }

        if (!epicInMap.getEpicSubtasksId().isEmpty()) {
            for(Integer subtaskId : epicInMap.getEpicSubtasksId()) {
                subtasks.add(idToSubtask.get(subtaskId));
            }
            return subtasks;
        }
        return subtasks;
    }

    public void deleteEpicSubtasks(Integer epicId) {
        Epic epicInMap;
        if (!idToEpic.containsKey(epicId)) {
            System.out.println("Map not contains epic ");
        } else {
            epicInMap = idToEpic.get(epicId);

            if (!getEpicSubtasks(epicId).isEmpty()) {
                epicInMap.clearSubtasks();
                refreshEpicStatus(epicId);
                System.out.println("Removed all subtasks from " + epicInMap.getName());
            } else {
                System.out.println("Epic not contains subtasks");
            }
        }
    }

    public void refreshEpicStatus(Integer epicId) { // вынести в менеджер
        int countNew = 0;
        int countDone = 0;

        if (getEpicSubtasks(epicId).isEmpty()) {
            getEpic(epicId).setStatus(TaskStatus.NEW);
        } else {
            for (Subtask subtask : getEpicSubtasks(epicId)) { // тут может кинуть null
                if (subtask.getStatus().equals(TaskStatus.NEW)) {
                    countNew++;
                } else if (subtask.getStatus().equals(TaskStatus.DONE)){
                    countDone++;
                }
            }

            if (countNew == getEpicSubtasks(epicId).size()) {
                getEpic(epicId).setStatus(TaskStatus.NEW);
            } else if (countDone == getEpicSubtasks(epicId).size()) {
                getEpic(epicId).setStatus(TaskStatus.DONE);
            } else {
                getEpic(epicId).setStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }

    private int generateNewId() {
        return taskId++;
    }
}
