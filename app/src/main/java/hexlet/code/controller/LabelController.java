package hexlet.code.controller;

import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.service.LabelService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LabelController {

    private final LabelService labelService;

    public LabelController(LabelService labelService) {
        this.labelService = labelService;
    }

    @GetMapping("/api/labels")
    public List<Label> index() {
        return labelService.getAll();
    }

    @GetMapping("/api/labels/{id}")
    public Label show(@PathVariable Long id) {
        return labelService.getById(id);
    }

    @PostMapping("/api/labels")
    @ResponseStatus(HttpStatus.CREATED)
    public Label create(@Valid @RequestBody LabelCreateDTO data) {
        return labelService.create(data);
    }

    @PutMapping("/api/labels/{id}")
    public Label update(@PathVariable Long id, @Valid @RequestBody LabelUpdateDTO data) {
        return labelService.update(id, data);
    }

    @DeleteMapping("/api/labels/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        labelService.delete(id);
    }
}
