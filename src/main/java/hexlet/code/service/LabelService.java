package hexlet.code.service;

import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.model.Label;
import java.util.List;

public interface LabelService {

    List<Label> getAll();

    Label getById(Long id);

    Label create(LabelCreateDTO data);

    Label update(Long id, LabelUpdateDTO data);

    void delete(Long id);

    void createDefaultLabels();
}
