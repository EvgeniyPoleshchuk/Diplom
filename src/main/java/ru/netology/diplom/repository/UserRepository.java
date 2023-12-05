package ru.netology.diplom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.diplom.model.UserData;

@Repository
public interface UserRepository extends JpaRepository<UserData,Long> {
    UserData findByEmail(String login);


}
