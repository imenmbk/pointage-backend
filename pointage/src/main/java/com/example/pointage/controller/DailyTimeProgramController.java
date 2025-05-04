package com.example.pointage.controller;

import com.example.pointage.model.DailyTimeProgram;
import com.example.pointage.service.DailyTimeProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "http://localhost:4200")

@RestController
@RequestMapping("/api/daily-programs")
@RequiredArgsConstructor
public class DailyTimeProgramController {

    private final DailyTimeProgramService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DailyTimeProgram create(@RequestBody DailyTimeProgram program) {
        return service.save(program);
    }

    @GetMapping
    public List<DailyTimeProgram> getAll() {
        return service.getAllPrograms();
    }

    @GetMapping("/{id}")
    public DailyTimeProgram getById(@PathVariable Long id) {
        return service.getProgramById(id)
                .orElseThrow(() -> new RuntimeException("Program not found"));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
