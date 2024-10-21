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
import java.util.Optional;
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

        if (startTime1 == null || endTime1 == null || startTime2 == null || endTime2 == null) {
            return false;
        }

        // вернуть проверку на null
        return startTime1.isBefore(endTime2) && endTime1.isAfter(startTime2)
                || startTime1.equals(startTime2) && endTime1.equals(endTime2);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return sortedTasks.stream().toList();
    }

    protected void addTaskInSet(Task task) {
        if (!(task instanceof Epic)) {
            sortedTasks.add(task);
        }
    }

    protected boolean isTaskTimeMatch(Task newTask) {
        // чтобы обновляемая задача не проверялась на пересечение с самой собой,
        // временно убираем старый вариант из списка
        Optional<Task> oldTask = getPrioritizedTasks().stream()
                .filter(task -> task.getId().equals(newTask.getId()))
                .findFirst();

        if (oldTask.isPresent()) {
            sortedTasks.remove(oldTask.get());

            if (getPrioritizedTasks().stream().anyMatch(task -> checkIntersection(newTask, task))) {
                sortedTasks.add(oldTask.get());
                return true;
            }
        }

        return getPrioritizedTasks().stream().anyMatch(task -> checkIntersection(newTask, task));
    }

    @Override
    public Task addNewTask(Task newTask) {
        int newId;

        if (Objects.nonNull(newTask)) {
            newId = generateNewId();
            newTask.setId(newId);

            if (isTaskTimeMatch(newTask)) {
                System.out.println("Task startTime is match with existent task (addNewTask)");
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
            System.out.println("Task is null (addNewTask)");
            return null;
        }
    }

    @Override
    public Epic addNewTask(Epic newEpic) {
        int newId;

        if (Objects.nonNull(newEpic)) {
            newId = generateNewId();
            newEpic.setId(newId);

            // эпик на пересечение не проверяем

            idEpic.put(newEpic.getId(), newEpic);
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

            if (isTaskTimeMatch(newSubtask)) {
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
                refreshEpicTimeOneSubtask(newSubtask);
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

        if (isTaskTimeMatch(updatedTask)) {
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
            System.out.println("Task is null (update)");
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
                if (subtaskUpdate.getEpicId() == null) {
                    subtaskUpdate.setEpicId(idEpic.get(subtaskMap.getEpicId()));
                }
            } else {
                System.out.println("Task with id " + subtaskId + " not exist");
                return null;
            }

            if (idSubtask.containsKey(subtaskId)
                    && subtaskUpdate.getEpicId().equals(idSubtask.get(subtaskId).getEpicId())) {

                if (subtaskUpdate.getStatus() == null) {
                    subtaskUpdate.setStatus(idSubtask.get(subtaskId).getStatus());
                }

                    if (isTaskTimeMatch(subtaskUpdate)) {
                        System.out.println("Updated subtask time is match with existent task");
                        return null;
                    }

                    idSubtask.put(subtaskId, subtaskUpdate);
                    refreshEpicStatus(subtaskUpdate.getEpicId());
                    refreshEpicTimeOneSubtask(subtaskUpdate);
                    System.out.println("Updated subtask: " + subtaskUpdate);

                    // если указан другой epic-id, то нужно удалить сабтаск у старого epic
                } else if (!subtaskUpdate.getEpicId().equals(idSubtask.get(subtaskId).getEpicId())) {
                    idEpic.get(idSubtask.get(subtaskId).getEpicId()).removeSubtask(subtaskId);
                    refreshEpicStatus(idSubtask.get(subtaskId).getEpicId());
                    refreshEpicTimeManySubtasks(idSubtask.get(subtaskId).getEpicId());

                    if (isTaskTimeMatch(subtaskUpdate)) {
                        System.out.println("Updated subtask time is match with existent task");
                        return null;
                    }

                    idSubtask.put(subtaskId, subtaskUpdate);
                    idEpic.get(subtaskUpdate.getEpicId()).addSubtask(subtaskUpdate);
                    refreshEpicTimeOneSubtask(subtaskUpdate);
                    refreshEpicStatus(subtaskUpdate.getEpicId());
                    System.out.println("Updated subtask: " + subtaskUpdate);


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
            System.out.println("Epic is null");
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

            } else {
                System.out.println("Task with id " + taskId + " not exist");
                return null;
            }

        } else {
            System.out.println("Id is null or less than 0");
            return null;
        }

        System.out.println("Removed task: " + removedTask + "(deleteTask)");
        return removedTask;
    }

    @Override
    public Subtask deleteSubtask(Integer subtaskId) {
        Subtask removedSubtask;

        if (Objects.nonNull(subtaskId) && subtaskId >= 0) {
            if (idSubtask.containsKey(subtaskId)) {
                Subtask subtask = idSubtask.get(subtaskId);
                idEpic.get(subtask.getEpicId()).removeSubtask(subtaskId);
                refreshEpicStatus(subtask.getEpicId());
                removedSubtask = idSubtask.remove(subtaskId);
                sortedTasks.remove(removedSubtask);
                refreshEpicTimeManySubtasks(removedSubtask.getEpicId());
                historyManager.remove(subtaskId);

            } else {
                System.out.println("Task with id " + subtaskId + " not exist");
                return null;
            }

        } else {
            System.out.println("Id is null or less than 0");
            return null;
        }

        System.out.println("Removed task: " + removedSubtask + "(deleteSubtask)");
        return removedSubtask;
    }


    @Override
    public Epic deleteEpic(Integer epicId) {
        Epic removedEpic;

        if (Objects.nonNull(epicId) && epicId >= 0) {

            if (idEpic.containsKey(epicId)) {
                removedEpic = idEpic.remove(epicId);
                sortedTasks.remove(removedEpic);
                historyManager.remove(epicId);

            } else {
                System.out.println("Task with id " + epicId + " not exist");
                return null;
            }

        } else {
            System.out.println("Id is null or less than 0");
            return null;
        }

        System.out.println("Removed task: " + removedEpic + "(deleteEpic)");
        return removedEpic;
    }

    @Override
    public void deleteAllTasks() {
        int tasksSum = 0;

        if (!idTask.isEmpty()) {
            tasksSum = idTask.size();
            idTask.keySet().forEach(historyManager::remove);
            sortedTasks.removeAll(idTask.values());
            idTask.clear();
        }

        System.out.println("Removed " + tasksSum + " tasks");
    }

    @Override
    public void deleteAllSubtasks() {
        int tasksSum = 0;

        if (!idSubtask.isEmpty()) {
            idSubtask.values().forEach(subtask -> {
                Epic epic = idEpic.get(subtask.getEpicId());
                epic.clearSubtasks();
                refreshEpicTimeManySubtasks(epic.getId());
                refreshEpicStatus(epic.getId());
            });

            tasksSum = idSubtask.size();
            idSubtask.keySet().forEach(historyManager::remove);
            sortedTasks.removeAll(idSubtask.values());
            idSubtask.clear();
        }

        System.out.println("Removed " + tasksSum + " subtasks");
    }

    @Override
    public void deleteAllEpic() {
        int tasksSum = 0;

        if (!idEpic.isEmpty()) {
            idEpic.values().forEach(epic -> epic.getEpicSubtasksId().forEach(subtaskId -> {
                idSubtask.remove(subtaskId);
                historyManager.remove(subtaskId);
            }));

            tasksSum = idEpic.size();
            idEpic.keySet().forEach(historyManager::remove);
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
            epicInMap.getEpicSubtasksId().forEach(subtaskId -> subtasks.add(idSubtask.get(subtaskId)));
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
                epicInMap.getEpicSubtasksId().forEach(historyManager::remove);
                epicInMap.getEpicSubtasksId().forEach(subtaskId -> sortedTasks.remove(idSubtask.remove(subtaskId)));

                epicInMap.clearSubtasks();
                refreshEpicTimeManySubtasks(epicId);
                refreshEpicStatus(epicId);
                System.out.println("Removed all subtasks from " + epicInMap.getName());

            } else {
                System.out.println("Epic not contains subtasks");
            }
        }
    }

    protected void refreshEpicStatus(Integer epicId) {
        List<Subtask> subtasks = getEpicSubtasks(epicId);

        if (subtasks.isEmpty()) {
            idEpic.get(epicId).setStatus(TaskStatus.NEW);
            return;
        }

        if (subtasks.stream().allMatch(subtask -> subtask.getStatus().equals(TaskStatus.NEW))) {
            idEpic.get(epicId).setStatus(TaskStatus.NEW);

        } else if (subtasks.stream().allMatch(subtask -> subtask.getStatus().equals(TaskStatus.DONE))) {
            idEpic.get(epicId).setStatus(TaskStatus.DONE);

        } else {
            idEpic.get(epicId).setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    protected void refreshEpicTimeOneSubtask(Subtask subtask) {
        Epic epic = idEpic.get(subtask.getEpicId());

        if (epic.getEpicSubtasksId().size() == 1) {
            epic.setStartTime(subtask.getStartTime());
        } else if (Objects.isNull(epic.getStartTime()) || epic.getStartTime().isAfter(subtask.getStartTime())) {
            epic.setStartTime(subtask.getStartTime());
        }

        if (epic.getEpicSubtasksId().size() == 1) {
            epic.setEndTime(subtask.getEndTime());
        } else if (Objects.isNull(epic.getEndTime()) || epic.getEndTime().isBefore(subtask.getEndTime())) {
            epic.setEndTime(subtask.getEndTime());
        }

        List<Subtask> subtasks = getEpicSubtasks(epic.getId());
        Duration duration = subtasks
                .stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);

        epic.setDuration(duration);
    }

    protected void refreshEpicTimeManySubtasks(Integer epicId) {
        Epic epic = idEpic.get(epicId);

        if (!epic.getEpicSubtasksId().isEmpty()) {

            List<Subtask> subtasks = getEpicSubtasks(epicId);

            LocalDateTime startTime = subtasks.stream()
                    .map(Subtask::getStartTime)
                    .filter(Objects::nonNull)
                    .min(LocalDateTime::compareTo)
                    .orElse(null);

            LocalDateTime endTime = subtasks.stream()
                    .map(Subtask::getEndTime)
                    .filter(Objects::nonNull)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);

            Duration duration = subtasks
                    .stream()
                    .map(Subtask::getDuration)
                    .filter(Objects::nonNull)
                    .reduce(Duration.ZERO, Duration::plus);

            epic.setStartTime(startTime);
            epic.setEndTime(endTime);
            epic.setDuration(duration);

        } else {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(Duration.ZERO);
        }
    }
}
