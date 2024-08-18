package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> idToTask = new HashMap<>();
    private final Map<Integer, Subtask> idToSubtask = new HashMap<>();
    private final Map<Integer, Epic> idToEpic = new HashMap<>();
    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }

    private Integer taskId = 1;

    private int generateNewId() {
        return taskId++;
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public Task addNewTask(Task newTask) {
        int newId;

        if (Objects.nonNull(newTask)) {
            newId = generateNewId();
            newTask.setId(newId);

            if (newTask.getClass() == Task.class) {
                idToTask.put(newTask.getId(), newTask);
                System.out.println("Added task: " + newTask);
                return newTask;

            } else {
                System.out.println("Received class is not Task");
                return null;
            }

        } else {
            System.out.println("Task is null");
            return null;
        }
    }

    @Override
    public Epic addNewTask(Epic newEpic) {
        int newId;

        if (Objects.nonNull(newEpic)) {
            newId = generateNewId();
            newEpic.setId(newId);
            idToEpic.put(newEpic.getId(), newEpic);
            System.out.println("Added epic: " + newEpic);
            return newEpic;

        } else {
            System.out.println("Epic is null");
            return null;
        }
    }

    @Override
    public Subtask addNewTask(Subtask newSubtask) {
        int newId;

        if (Objects.nonNull(newSubtask)) {
            Integer subtaskEpicId = newSubtask.getEpicId();
            newId = generateNewId();
            newSubtask.setId(newId);

            if (subtaskEpicId != null && subtaskEpicId > 0) {
                if (idToEpic.containsKey(newSubtask.getEpicId())) {
                    idToEpic.get(newSubtask.getEpicId()).addSubtask(newSubtask);
                } else {
                    System.out.println("Map not contains epic");
                    return null;
                }
                idToSubtask.put(newSubtask.getId(), newSubtask);
                refreshEpicStatus(newSubtask.getEpicId());
                System.out.println("Added subtask: " + newSubtask);
                return newSubtask;
            } else {
                System.out.println("Subtask must contain Epic ");
                return null;
            }
        } else {
            System.out.println("Subtask is null");
            return null;
        }
    }

    @Override
    public Task updateTask(Task updatedTask) {
        int taskId;

        if (Objects.nonNull(updatedTask)) {
            taskId = updatedTask.getId();

            if (idToTask.containsKey(taskId) && updatedTask.getClass() == Task.class) {

                if (updatedTask.getStatus() == null) {
                    updatedTask.setStatus(idToTask.get(taskId).getStatus());
                }
                idToTask.put(taskId, updatedTask);
                System.out.println("Updated task: " + updatedTask);
                return updatedTask;

            } else {
                System.out.println("Task with id " + taskId + " not exist");
                return null;
            }

        } else {
            System.out.println("Task is null");
            return null;
        }
    }

    @Override
    public Subtask updateTask(Subtask subtaskUpdate) {
        int subtaskId;

        if (Objects.nonNull(subtaskUpdate)) {
            subtaskId = subtaskUpdate.getId();

            if (idToSubtask.containsKey(subtaskId)) {
                Subtask subtaskMap = idToSubtask.get(subtaskId);

                if (subtaskUpdate.getStatus() == null) {
                    subtaskUpdate.setStatus(idToSubtask.get(subtaskId).getStatus());
                }

                if (subtaskUpdate.getEpicId() == null) {
                    subtaskUpdate.setEpicId(idToEpic.get(subtaskMap.getEpicId()));
                    idToSubtask.put(subtaskId, subtaskUpdate);
                    refreshEpicStatus(subtaskUpdate.getEpicId());
                    System.out.print("Updated subtask: " + subtaskUpdate);

                    // если указан другой epic-id, то нужно удалить сабтаск у старого epic
                } else if (!subtaskUpdate.getEpicId().equals(idToSubtask.get(subtaskId).getEpicId())) {
                    idToEpic.get(idToSubtask.get(subtaskId).getEpicId()).removeSubtask(subtaskId);
                    refreshEpicStatus(idToSubtask.get(subtaskId).getEpicId());
                    idToSubtask.put(subtaskId, subtaskUpdate);
                    idToEpic.get(subtaskUpdate.getEpicId()).addSubtask(subtaskUpdate);
                    refreshEpicStatus(subtaskUpdate.getEpicId());
                    System.out.println("Updated subtask: " + subtaskUpdate);

                }

            } else {
                System.out.println("Task with id " + subtaskId + " not exist");
                return null;
            }

        } else {
            System.out.println("Subtask is null");
            return null;
        }

        return subtaskUpdate;
    }

    @Override
    public Epic updateTask(Epic epicUpdate) {
        int epicId;

        if (Objects.nonNull(epicUpdate)) {
            epicId = epicUpdate.getId();

            if (idToEpic.containsKey(epicId)) {
                epicUpdate.replaceSubtasks(idToEpic.get(epicId).getEpicSubtasksId());
                idToEpic.put(epicId, epicUpdate);
                System.out.println("Updated epic: " + epicUpdate);
                return epicUpdate;

            } else {
                System.out.println("Task with id " + epicId + " not exist");
                return null;
            }

        } else {
            System.out.println("Subtask is null");
            return null;
        }
    }

    @Override
    public Task deleteTask(Integer taskId) {
        Task removedTask;

        if (Objects.nonNull(taskId) && taskId > 0) {

            if (idToTask.containsKey(taskId)) {
                removedTask = idToTask.get(taskId);
                idToTask.remove(taskId);

            } else if (idToSubtask.containsKey(taskId)) {
                Subtask subtask = idToSubtask.get(taskId);
                idToEpic.get(subtask.getEpicId()).removeSubtask(taskId);
                refreshEpicStatus(subtask.getEpicId());
                removedTask = idToSubtask.get(taskId);
                idToSubtask.remove(taskId);

            } else if (idToEpic.containsKey(taskId)) {
                removedTask = idToEpic.get(taskId);
                idToEpic.remove(taskId);

            } else {
                System.out.println("Task with id " + taskId + " not exist");
                return null;
            }

        } else {
            System.out.println("Task is null or id less than 0");
            return null;
        }

        System.out.println("Removed task: " + removedTask);
        return removedTask;
    }

    @Override
    public void deleteAllTasks() {
        int tasksSum = 0;

        if (!idToTask.isEmpty()) {
            tasksSum = idToTask.size();
            idToTask.clear();
        }

        System.out.println("Removed " + tasksSum + " tasks");
    }

    @Override
    public void deleteAllSubtasks() {
        int tasksSum = 0;

        if (!idToSubtask.isEmpty()) {
            Epic epic;
            for (Subtask subtask : idToSubtask.values()) {
                epic = idToEpic.get(subtask.getEpicId());
                epic.clearSubtasks();
                refreshEpicStatus(epic.getId());
            }
            tasksSum = idToSubtask.size();
            idToSubtask.clear();
        }

        System.out.println("Removed " + tasksSum + " subtasks");
    }

    @Override
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

    @Override
    public ArrayList<Task> getAllTasks() {
        if (!idToTask.isEmpty()) {
            return new ArrayList<>(idToTask.values());
        }
        System.out.print("Tasks list is empty: ");
        return null;
    }

    @Override
    public ArrayList<Task> getAllSubtasks() {
        if (!idToSubtask.isEmpty()) {
            return new ArrayList<>(idToSubtask.values());
        }
        System.out.print("Subtasks list is empty: ");
        return null;
    }

    @Override
    public ArrayList<Task> getAllEpic() {
        if (!idToEpic.isEmpty()) {
            return new ArrayList<>(idToEpic.values());
        }
        System.out.print("Epic tasks list is empty: ");
        return null;
    }

    @Override
    public Task getTask(Integer taskId) {
        if (Objects.nonNull(idToTask.get(taskId))) {
            historyManager.add(idToTask.get(taskId));
            return idToTask.get(taskId);
        } else {
            System.out.println("There is not task-id " + taskId);
            return null;
        }
    }

    @Override
    public Epic getEpic(Integer epicId) {
        if (Objects.nonNull(idToEpic.get(epicId))) {
            historyManager.add(idToEpic.get(epicId));
            return idToEpic.get(epicId);
        } else {
            System.out.println("There is not epic-id " + epicId);
            return null;
        }
    }

    @Override
    public Subtask getSubtask(Integer subtaskId) {
        if (Objects.nonNull(idToSubtask.get(subtaskId))) {
            historyManager.add(idToSubtask.get(subtaskId));
            return idToSubtask.get(subtaskId);
        } else {
            System.out.println("There is not subtask-id " + subtaskId);
            return null;
        }
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(Integer epicId) {
        Epic epicInMap;
        ArrayList<Subtask> subtasks = new ArrayList<>();

        if (!Objects.nonNull(idToEpic.get(epicId))) {
            System.out.print("Not Epic with this id: ");
            return null;

        } else {
            epicInMap = idToEpic.get(epicId);
        }

        if (Objects.nonNull(epicInMap.getEpicSubtasksId())) {
            for (Integer subtaskId : epicInMap.getEpicSubtasksId()) {
                subtasks.add(idToSubtask.get(subtaskId));
            }
        }
        return subtasks;
    }

    @Override
    public void deleteEpicSubtasks(Integer epicId) {
        Epic epicInMap;
        if (!Objects.nonNull(idToEpic.get(epicId))) {
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

    private void refreshEpicStatus(Integer epicId) {
        int countNew = 0;
        int countDone = 0;

        if (getEpicSubtasks(epicId).isEmpty()) {
            idToEpic.get(epicId).setStatus(TaskStatus.NEW);
        } else {
            for (Subtask subtask : getEpicSubtasks(epicId)) { // тут может кинуть null
                if (subtask.getStatus().equals(TaskStatus.NEW)) {
                    countNew++;
                } else if (subtask.getStatus().equals(TaskStatus.DONE)) {
                    countDone++;
                }
            }

            if (countNew == getEpicSubtasks(epicId).size()) {
                idToEpic.get(epicId).setStatus(TaskStatus.NEW);
            } else if (countDone == getEpicSubtasks(epicId).size()) {
                idToEpic.get(epicId).setStatus(TaskStatus.DONE);
            } else {
                idToEpic.get(epicId).setStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }
}
