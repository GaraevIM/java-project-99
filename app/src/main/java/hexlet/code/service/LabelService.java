package hexlet.code.service;

import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.exception.ResourceConflictException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LabelService {

    private final LabelRepository labelRepository;

    private final TaskRepository taskRepository;

    public LabelService(LabelRepository labelRepository, TaskRepository taskRepository) {
        this.labelRepository = labelRepository;
        this.taskRepository = taskRepository;
    }

    public List<Label> getAll() {
        return labelRepository.findAll();
    }

    public Label getById(Long id) {
        return labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found"));
    }

    public Label create(LabelCreateDTO data) {
        var label = new Label();
        label.setName(data.getName());
        return labelRepository.save(label);
    }

    public Label update(Long id, LabelUpdateDTO data) {
        var label = getById(id);

        if (data.getName() != null) {
            label.setName(data.getName());
        }

        return labelRepository.save(label);
    }

    public void delete(Long id) {
        var label = getById(id);

        if (taskRepository.existsByLabelsContaining(label)) {
            throw new ResourceConflictException("Label has tasks");
        }

        labelRepository.delete(label);
    }

    public void createDefaultLabels() {
        createIfNotExists("feature");
        createIfNotExists("bug");
    }

    private void createIfNotExists(String name) {
        if (!labelRepository.existsByName(name)) {
            var data = new LabelCreateDTO();
            data.setName(name);
            create(data);
        }
    }
}
