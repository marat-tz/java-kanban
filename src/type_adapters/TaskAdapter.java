package type_adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskAdapter extends TypeAdapter<Task> {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE_TIME;

    @Override
    public void write(JsonWriter jsonWriter, Task task) throws IOException {

        if (!(task instanceof Subtask)) {
            jsonWriter
                    .beginObject()
                    .name("id").value(task.getId())
                    .name("type").value(task.getType().toString())
                    .name("name").value(task.getName())
                    .name("description").value(task.getDescription())
                    .name("status").value(task.getStatus().toString())
                    .name("duration").value(task.getDuration().toMinutes())
                    .name("startTime").value(task.getStartTime().format(dtf))
                    .name("endTime").value(task.getEndTime().format(dtf))
                    .endObject();
        } else {
            jsonWriter
                    .beginObject()
                    .name("id").value(task.getId())
                    .name("type").value(task.getType().toString())
                    .name("name").value(task.getName())
                    .name("description").value(task.getDescription())
                    .name("status").value(task.getStatus().toString())
                    .name("duration").value(task.getDuration().toMinutes())
                    .name("startTime").value(task.getStartTime().format(dtf))
                    .name("endTime").value(task.getEndTime().format(dtf))
                    .name("epicId").value(((Subtask) task).getEpicId().toString())
                    .endObject();
        }
    }

    @Override
    public Task read(JsonReader jsonReader) throws IOException {
        StringBuilder fullString = new StringBuilder();

        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            jsonReader.nextName();
            fullString.append(jsonReader.nextString()).append(",");
        }
        System.out.println(fromString(fullString.toString()));
        return fromString(fullString.toString());
    }

    private Task fromString(String value) {
        String[] fields = value.split(",");
        Integer id = null;
        String name;
        String description;
        TaskStatus status;
        LocalDateTime startTime;
        Duration duration;

        boolean isFirstFieldNumber;
        try {
            id = Integer.parseInt(fields[2]);
            isFirstFieldNumber = true;
        } catch (NumberFormatException ex) {
            isFirstFieldNumber = false;
        }

        if (isFirstFieldNumber) {
            name = fields[0];
            description = fields[1];

            status = switch (fields[3]) {
                case "IN_PROGRESS" -> TaskStatus.IN_PROGRESS;
                case "DONE" -> TaskStatus.DONE;
                default -> TaskStatus.NEW;
            };

            duration = Duration.ofMinutes(Integer.parseInt(fields[4]));
            startTime = LocalDateTime.parse(fields[5], dtf);

            return new Task(id, name, description, status, duration, startTime);

        } else {
            name = fields[0];
            description = fields[1];

            status = switch (fields[2]) {
                case "IN_PROGRESS" -> TaskStatus.IN_PROGRESS;
                case "DONE" -> TaskStatus.DONE;
                default -> TaskStatus.NEW;
            };

            duration = Duration.ofMinutes(Integer.parseInt(fields[3]));
            startTime = LocalDateTime.parse(fields[4], dtf);

            return new Task(name, description, status, duration, startTime);
        }
    }

}