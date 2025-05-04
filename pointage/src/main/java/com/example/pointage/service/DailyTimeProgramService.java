package com.example.pointage.service;

import com.example.pointage.model.DailyTimeProgram;
import com.example.pointage.repository.DailyTimeProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DailyTimeProgramService {

    private final DailyTimeProgramRepository repository;

    public DailyTimeProgram save(DailyTimeProgram program) {
        return repository.save(program);
    }

    public List<DailyTimeProgram> getAllPrograms() {
        return repository.findAll();
    }

    public Optional<DailyTimeProgram> getProgramById(Long id) {
        return repository.findById(id);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
