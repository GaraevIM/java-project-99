package hexlet.code.component;

import hexlet.code.service.TaskStatusService;
import hexlet.code.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;

    private final TaskStatusService taskStatusService;

    public DataInitializer(UserService userService, TaskStatusService taskStatusService) {
        this.userService = userService;
        this.taskStatusService = taskStatusService;
    }

    @Override
    public void run(String... args) {
        userService.createAdminIfNotExists();
        taskStatusService.createDefaultStatuses();
    }
}
