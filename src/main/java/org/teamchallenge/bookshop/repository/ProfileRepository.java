package org.teamchallenge.bookshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.teamchallenge.bookshop.model.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
