package hexlet.code.component;

import hexlet.code.service.LabelService;
import hexlet.code.service.TaskStatusService;
import hexlet.code.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;

    private final TaskStatusService taskStatusService;

    private final LabelService labelService;

    public DataInitializer(
            UserService userService,
            TaskStatusService taskStatusService,
            LabelService labelService
    ) {
        this.userService = userService;
        this.taskStatusService = taskStatusService;
        this.labelService = labelService;
    }

    @Override
    public void run(String... args) {
        userService.createAdminIfNotExists();
        taskStatusService.createDefaultStatuses();
        labelService.createDefaultLabels();
    }
}
