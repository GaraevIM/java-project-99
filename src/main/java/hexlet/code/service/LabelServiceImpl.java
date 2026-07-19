package hexlet.code.service;

import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service("labelService")
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;

    private final TaskRepository taskRepository;

    private final LabelMapper labelMapper;

    public LabelServiceImpl(
            LabelRepository labelRepository,
            TaskRepository taskRepository,
            LabelMapper labelMapper
    ) {
        this.labelRepository = labelRepository;
        this.taskRepository = taskRepository;
        this.labelMapper = labelMapper;
    }

    @Override
    public List<Label> getAll() {
        return labelRepository.findAll();
    }

    @Override
    public Label getById(Long id) {
        return labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label not found"));
    }

    @Override
    public Label create(LabelCreateDTO data) {
        var label = labelMapper.map(data);
        return labelRepository.save(label);
    }

    @Override
    public Label update(Long id, LabelUpdateDTO data) {
        var label = getById(id);

        if (data.getName() != null) {
            label.setName(data.getName());
        }

        return labelRepository.save(label);
    }

    @Override
    public void delete(Long id) {
        var label = getById(id);
        labelRepository.delete(label);
    }

    @Override
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
