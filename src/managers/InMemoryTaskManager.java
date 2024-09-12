package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> idTask = new HashMap<>();
    private final Map<Integer, Subtask> idSubtask = new HashMap<>();
    private final Map<Integer, Epic> idEpic = new HashMap<>();
    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }

    private Integer taskId = 0;

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

            // в данный метод должны попадать только типы Task
            // для потомков созданы отдельные методы, с целью уменьшить вероятность ошибок
            if (newTask.getClass() == Task.class) {
                idTask.put(newTask.getId(), newTask);
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
            idEpic.put(newEpic.getId(), newEpic);
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

            if (subtaskEpicId != null && subtaskEpicId >= 0) {
                if (idEpic.containsKey(newSubtask.getEpicId())) {
                    idEpic.get(newSubtask.getEpicId()).addSubtask(newSubtask);
                } else {
                    System.out.println("Map not contains epic");
                    return null;
                }
                idSubtask.put(newSubtask.getId(), newSubtask);
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

            if (idTask.containsKey(taskId) && updatedTask.getClass() == Task.class) {

                if (updatedTask.getStatus() == null) {
                    updatedTask.setStatus(idTask.get(taskId).getStatus());
                }
                idTask.put(taskId, updatedTask);
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

            if (idSubtask.containsKey(subtaskId)) {
                Subtask subtaskMap = idSubtask.get(subtaskId);

                if (subtaskUpdate.getStatus() == null) {
                    subtaskUpdate.setStatus(idSubtask.get(subtaskId).getStatus());
                }

                if (subtaskUpdate.getEpicId() == null) {
                    subtaskUpdate.setEpicId(idEpic.get(subtaskMap.getEpicId()));
                    idSubtask.put(subtaskId, subtaskUpdate);
                    refreshEpicStatus(subtaskUpdate.getEpicId());
                    System.out.print("Updated subtask: " + subtaskUpdate);

                    // если указан другой epic-id, то нужно удалить сабтаск у старого epic
                } else if (!subtaskUpdate.getEpicId().equals(idSubtask.get(subtaskId).getEpicId())) {
                    idEpic.get(idSubtask.get(subtaskId).getEpicId()).removeSubtask(subtaskId);
                    refreshEpicStatus(idSubtask.get(subtaskId).getEpicId());
                    idSubtask.put(subtaskId, subtaskUpdate);
                    idEpic.get(subtaskUpdate.getEpicId()).addSubtask(subtaskUpdate);
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

            if (idEpic.containsKey(epicId)) {
                epicUpdate.replaceSubtasks(idEpic.get(epicId).getEpicSubtasksId());
                idEpic.put(epicId, epicUpdate);
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

        if (Objects.nonNull(taskId) && taskId >= 0) {

            if (idTask.containsKey(taskId)) {
                removedTask = idTask.remove(taskId);
                historyManager.remove(taskId);

            } else if (idSubtask.containsKey(taskId)) {
                Subtask subtask = idSubtask.get(taskId);
                idEpic.get(subtask.getEpicId()).removeSubtask(taskId);
                refreshEpicStatus(subtask.getEpicId());
                removedTask = idSubtask.remove(taskId);
                historyManager.remove(taskId);

            } else if (idEpic.containsKey(taskId)) {
                removedTask = idEpic.remove(taskId);
                historyManager.remove(taskId);

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

        if (!idTask.isEmpty()) {
            tasksSum = idTask.size();
            for (int id : idTask.keySet()) {
                historyManager.remove(id);
            }
            idTask.clear();
        }

        System.out.println("Removed " + tasksSum + " tasks");
    }

    @Override
    public void deleteAllSubtasks() {
        int tasksSum = 0;

        if (!idSubtask.isEmpty()) {
            Epic epic;
            for (Subtask subtask : idSubtask.values()) {
                epic = idEpic.get(subtask.getEpicId());
                epic.clearSubtasks();
                refreshEpicStatus(epic.getId());
            }
            tasksSum = idSubtask.size();
            for (int id : idSubtask.keySet()) {
                historyManager.remove(id);
            }
            idSubtask.clear();
        }

        System.out.println("Removed " + tasksSum + " subtasks");
    }

    @Override
    public void deleteAllEpic() {
        int tasksSum = 0;

        if (!idEpic.isEmpty()) {
            for (Epic epic : idEpic.values()) {
                for (Integer subtaskId : epic.getEpicSubtasksId()) {
                    idSubtask.remove(subtaskId);
                    historyManager.remove(subtaskId);
                }
            }
            tasksSum = idEpic.size();
            for (int id : idEpic.keySet()) {
                historyManager.remove(id);
            }
            idEpic.clear();
        }

        System.out.println("Removed " + tasksSum + " epics");
    }

    @Override
    public List<Task> getAllTasks() {
        if (!idTask.isEmpty()) {
            return new ArrayList<>(idTask.values());
        }
        System.out.print("Tasks list is empty: ");
        return null;
    }

    @Override
    public List<Task> getAllSubtasks() {
        if (!idSubtask.isEmpty()) {
            return new ArrayList<>(idSubtask.values());
        }
        System.out.print("Subtasks list is empty: ");
        return null;
    }

    @Override
    public List<Task> getAllEpic() {
        if (!idEpic.isEmpty()) {
            return new ArrayList<>(idEpic.values());
        }
        System.out.print("Epic tasks list is empty: ");
        return null;
    }

    @Override
    public Task getTask(Integer taskId) {
        if (Objects.nonNull(idTask.get(taskId))) {
            historyManager.add(idTask.get(taskId));
            return idTask.get(taskId);
        } else {
            System.out.println("There is not task-id " + taskId);
            return null;
        }
    }

    @Override
    public Epic getEpic(Integer epicId) {
        if (Objects.nonNull(idEpic.get(epicId))) {
            historyManager.add(idEpic.get(epicId));
            return idEpic.get(epicId);
        } else {
            System.out.println("There is not epic-id " + epicId);
            return null;
        }
    }

    @Override
    public Subtask getSubtask(Integer subtaskId) {
        if (Objects.nonNull(idSubtask.get(subtaskId))) {
            historyManager.add(idSubtask.get(subtaskId));
            return idSubtask.get(subtaskId);
        } else {
            System.out.println("There is not subtask-id " + subtaskId);
            return null;
        }
    }

    @Override
    public List<Subtask> getEpicSubtasks(Integer epicId) {
        Epic epicInMap;
        ArrayList<Subtask> subtasks = new ArrayList<>();

        if (!Objects.nonNull(idEpic.get(epicId))) {
            System.out.print("Not Epic with this id: ");
            return null;

        } else {
            epicInMap = idEpic.get(epicId);
        }

        if (Objects.nonNull(epicInMap.getEpicSubtasksId())) {
            for (Integer subtaskId : epicInMap.getEpicSubtasksId()) {
                subtasks.add(idSubtask.get(subtaskId));
            }
        }
        return subtasks;
    }

    @Override
    public void deleteEpicSubtasks(Integer epicId) {
        Epic epicInMap;
        if (!Objects.nonNull(idEpic.get(epicId))) {
            System.out.println("Map not contains epic ");

        } else {
            epicInMap = idEpic.get(epicId);
            if (!getEpicSubtasks(epicId).isEmpty()) {

                for (Subtask subtask : getEpicSubtasks(epicId)) {
                    // TODO: Оптимизировать удаление сабтасков
                    historyManager.remove(subtask.getId());
                    idSubtask.remove(subtask.getId());
                }

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
            idEpic.get(epicId).setStatus(TaskStatus.NEW);
        } else {
            for (Subtask subtask : getEpicSubtasks(epicId)) { // тут может кинуть null
                if (subtask.getStatus().equals(TaskStatus.NEW)) {
                    countNew++;
                } else if (subtask.getStatus().equals(TaskStatus.DONE)) {
                    countDone++;
                }
            }

            if (countNew == getEpicSubtasks(epicId).size()) {
                idEpic.get(epicId).setStatus(TaskStatus.NEW);
            } else if (countDone == getEpicSubtasks(epicId).size()) {
                idEpic.get(epicId).setStatus(TaskStatus.DONE);
            } else {
                idEpic.get(epicId).setStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }
}
