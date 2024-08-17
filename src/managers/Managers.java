package managers;

public class Managers {

    public static TaskManager getDefault() {
        HistoryManager historyManager = getDefaultHistory();
        return new InMemoryTaskManager(historyManager);
    }

    private static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }


}
