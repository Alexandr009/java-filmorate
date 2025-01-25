package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class MpaService {

    @Autowired
    private MpaDbStorage mpaDbStorage;

    @Autowired
    public MpaService(MpaDbStorage mpaDbStorage) {
        this.mpaDbStorage = mpaDbStorage;
    }

    public Mpa getMpaById(int id) {
        Optional<Mpa> mpa = mpaDbStorage.getMpaById(id);
        if (mpa.isEmpty()) {
            throw new NotFoundException(String.format("mpa with id = %s not found", id));
        }
        return mpa.orElse(null);
    }

    public List<Mpa> getMpa() {
        return mpaDbStorage.getMpa();
    }

}
