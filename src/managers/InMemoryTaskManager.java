package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {
    protected Map<Integer, Task> idTask = new HashMap<>();
    protected Map<Integer, Subtask> idSubtask = new HashMap<>();
    protected Map<Integer, Epic> idEpic = new HashMap<>();
    protected Set<Task> sortedTasks = new TreeSet<>();
    protected final HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }

    protected Integer taskId = 0;

    protected int generateNewId() {
        return taskId++;
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    protected boolean checkIntersection(Task task1, Task task2) {
        LocalDateTime startTime1 = task1.getStartTime();
        LocalDateTime endTime1 = task1.getEndTime();
        LocalDateTime startTime2 = task2.getStartTime();
        LocalDateTime endTime2 = task2.getEndTime();

        Subtask tempSubtask;
        Epic tempEpic;

        if (Objects.equals(task1.getId(), task2.getId())) {
            return false;
        }

        if (task1 instanceof Subtask && task2 instanceof Epic) {
            tempSubtask = (Subtask) task1;
            tempEpic = (Epic) task2;
            if (tempSubtask.getEpicId().equals(tempEpic.getId())) {
                return false;
            }
        } else if (task2 instanceof Subtask && task1 instanceof Epic) {
            tempSubtask = (Subtask) task2;
            tempEpic = (Epic) task1;
            if (tempSubtask.getEpicId().equals(tempEpic.getId())) {
                return false;
            }
        }

        if (startTime1.isBefore(startTime2) && endTime1.isBefore(startTime2)
                || startTime2.isBefore(startTime1) && endTime2.isBefore(startTime1)) {
            return false;

        } else if (startTime1.isBefore(startTime2) && endTime1.isAfter(startTime2)
                || startTime2.isBefore(startTime1) && endTime2.isAfter(startTime1)){
            return true;

        } else {
            return true;
        }
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return sortedTasks.stream().toList();
    }

    protected void addTaskInSet(Task task) {
        if (Objects.nonNull(task.getStartTime()) && !sortedTasks.contains(task)) {
            sortedTasks.add(task);
        } else {
            System.out.println("Task is null or already contained");
        }
    }

    protected boolean isTaskStartTimeMatch(Task newTask) {
        return getPrioritizedTasks().stream().anyMatch(task -> checkIntersection(newTask, task));
    }

    @Override
    public Task addNewTask(Task newTask) {
        int newId;

        if (Objects.nonNull(newTask)) {
            newId = generateNewId();
            newTask.setId(newId);

            if (isTaskStartTimeMatch(newTask)) {
                System.out.println("Task startTime is match with existent task");
                return null;
            }

            // в данный метод должны попадать только типы Task
            // для потомков созданы отдельные методы, с целью уменьшить вероятность ошибок
            if (newTask.getClass() == Task.class) {
                idTask.put(newTask.getId(), newTask);
                addTaskInSet(newTask);
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

            if (isTaskStartTimeMatch(newEpic)) {
                System.out.println("Epic startTime is match with existent epic");
                return null;
            }

            idEpic.put(newEpic.getId(), newEpic);
            refreshEpicTimeRemoveSubtask(newEpic.getId());
            addTaskInSet(newEpic);
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

            if (isTaskStartTimeMatch(newSubtask)) {
                System.out.println("Subtask startTime is match with existent subtask");
                return null;
            }

            if (subtaskEpicId != null && subtaskEpicId >= 0) {
                if (idEpic.containsKey(newSubtask.getEpicId())) {
                    idEpic.get(newSubtask.getEpicId()).addSubtask(newSubtask);
                } else {
                    System.out.println("Map not contains epic");
                    return null;
                }
                idSubtask.put(newSubtask.getId(), newSubtask);
                refreshEpicStatus(newSubtask.getEpicId());
                refreshEpicTime(newSubtask);
                addTaskInSet(newSubtask);
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

        if (isTaskStartTimeMatch(updatedTask)) {
            System.out.println("Updated task startTime is match with existent task");
            return null;
        }

        if (Objects.nonNull(updatedTask)) {
            taskId = updatedTask.getId();

            if (idTask.containsKey(taskId) && updatedTask.getClass() == Task.class) {

                if (updatedTask.getStatus() == null) {
                    updatedTask.setStatus(idTask.get(taskId).getStatus());
                }
                idTask.put(taskId, updatedTask);
                addTaskInSet(updatedTask);
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

                    if (isTaskStartTimeMatch(subtaskUpdate)) {
                        System.out.println("Updated subtask time is match with existent task");
                        return null;
                    }

                    idSubtask.put(subtaskId, subtaskUpdate);
                    refreshEpicStatus(subtaskUpdate.getEpicId());
                    refreshEpicTime(subtaskUpdate);
                    System.out.print("Updated subtask: " + subtaskUpdate);

                    // если указан другой epic-id, то нужно удалить сабтаск у старого epic
                } else if (!subtaskUpdate.getEpicId().equals(idSubtask.get(subtaskId).getEpicId())) {
                    idEpic.get(idSubtask.get(subtaskId).getEpicId()).removeSubtask(subtaskId);
                    refreshEpicStatus(idSubtask.get(subtaskId).getEpicId());
                    refreshEpicTimeRemoveSubtask(idSubtask.get(subtaskId).getEpicId());

                    if (isTaskStartTimeMatch(subtaskUpdate)) {
                        System.out.println("Updated subtask time is match with existent task");
                        return null;
                    }

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

        addTaskInSet(subtaskUpdate);
        return subtaskUpdate;
    }

    @Override
    public Epic updateTask(Epic epicUpdate) {
        int epicId;

        if (isTaskStartTimeMatch(epicUpdate)) {
            System.out.println("Updated epic startTime is match with existent epic");
            return null;
        }

        if (Objects.nonNull(epicUpdate)) {
            epicId = epicUpdate.getId();

            if (idEpic.containsKey(epicId)) {
                epicUpdate.replaceSubtasks(idEpic.get(epicId).getEpicSubtasksId());
                idEpic.put(epicId, epicUpdate);
                addTaskInSet(epicUpdate);
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
                sortedTasks.remove(removedTask);
                historyManager.remove(taskId);

            } else if (idSubtask.containsKey(taskId)) {
                Subtask subtask = idSubtask.get(taskId);
                idEpic.get(subtask.getEpicId()).removeSubtask(taskId);
                refreshEpicStatus(subtask.getEpicId());
                removedTask = idSubtask.remove(taskId);
                sortedTasks.remove(removedTask);
                refreshEpicTimeRemoveSubtask(((Subtask) removedTask).getEpicId());
                historyManager.remove(taskId);

            } else if (idEpic.containsKey(taskId)) {
                removedTask = idEpic.remove(taskId);
                sortedTasks.remove(removedTask);
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
            sortedTasks.removeAll(idTask.values());
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
                epic.setStartTime(null);
                epic.setEndTime(null);
                refreshEpicStatus(epic.getId());
            }
            tasksSum = idSubtask.size();
            for (int id : idSubtask.keySet()) {
                historyManager.remove(id);
            }
            sortedTasks.removeAll(idSubtask.values());
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
            sortedTasks.removeAll(idEpic.values());
            idEpic.clear();
        }

        System.out.println("Removed " + tasksSum + " epics");
    }

    @Override
    public List<Task> getAllTasks() {
        if (!idTask.isEmpty()) {
            return new ArrayList<>(idTask.values());
        }
        return new ArrayList<>();
    }

    @Override
    public List<Task> getAllSubtasks() {
        if (!idSubtask.isEmpty()) {
            return new ArrayList<>(idSubtask.values());
        }
        return new ArrayList<>();
    }

    @Override
    public List<Task> getAllEpic() {
        if (!idEpic.isEmpty()) {
            return new ArrayList<>(idEpic.values());
        }
        return new ArrayList<>();
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
        Task removedTask;

        if (!Objects.nonNull(idEpic.get(epicId))) {
            System.out.println("Map not contains epic ");

        } else {
            epicInMap = idEpic.get(epicId);
            if (!getEpicSubtasks(epicId).isEmpty()) {

                for (Subtask subtask : getEpicSubtasks(epicId)) {
                    historyManager.remove(subtask.getId());
                    removedTask = idSubtask.remove(subtask.getId());
                    epicInMap.setStartTime(null);
                    epicInMap.setEndTime(null);
                    sortedTasks.remove(removedTask);
                }

                epicInMap.clearSubtasks();
                refreshEpicStatus(epicId);
                System.out.println("Removed all subtasks from " + epicInMap.getName());
            } else {
                System.out.println("Epic not contains subtasks");
            }
        }
    }

    protected void refreshEpicStatus(Integer epicId) {
        int countNew = 0;
        int countDone = 0;

        if (getEpicSubtasks(epicId).isEmpty()) {
            idEpic.get(epicId).setStatus(TaskStatus.NEW);
        } else {
                for (Subtask subtask : getEpicSubtasks(epicId)) { // тут может кинуть null
                    if (subtask != null) {
                        if (subtask.getStatus().equals(TaskStatus.NEW)) {
                            countNew++;
                        } else if (subtask.getStatus().equals(TaskStatus.DONE)) {
                            countDone++;
                        }
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

    protected void refreshEpicTime(Subtask subtask) {
        Epic epic = idEpic.get(subtask.getEpicId());

        if (Objects.isNull(epic.getStartTime()) || epic.getStartTime().isAfter(subtask.getStartTime())) {
            epic.setStartTime(subtask.getStartTime());
        }

        if (Objects.isNull(epic.getEndTime()) || epic.getEndTime().isBefore(subtask.getEndTime())) {
            epic.setEndTime(subtask.getEndTime());
        }

        epic.setDuration(Duration.between(epic.getStartTime(), epic.getEndTime()));
    }

    protected void refreshEpicTimeRemoveSubtask(Integer epicId) {
        Epic epic = idEpic.get(epicId);
        if (!epic.getEpicSubtasksId().isEmpty()) {
            List<Integer> epicSubtasksId = epic.getEpicSubtasksId();
            epic.setStartTime(null);
            epic.setEndTime(null);
            for (Integer id : epicSubtasksId) {
                Subtask tempSubtask = idSubtask.get(id);
                refreshEpicTime(tempSubtask);
            }
        } else {
            epic.setEndTime(epic.getStartTime());
        }
    }
}
