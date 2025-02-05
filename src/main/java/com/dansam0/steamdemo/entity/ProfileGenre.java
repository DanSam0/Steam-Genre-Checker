package com.dansam0.steamdemo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "profiles_genres")
@Data
@NoArgsConstructor
@AssociationOverrides({
        @AssociationOverride(name = "primaryKey.profile",
                joinColumns = @JoinColumn(name = "profile_id")),
        @AssociationOverride(name = "primaryKey.genre",
                joinColumns = @JoinColumn(name = "genre_id"))
})
public class ProfileGenre {

    @EmbeddedId
    private ProfileGenreId primaryKey = new ProfileGenreId();

    @Column(name = "playtime")
    private int playtime;

    @Transient
    public Profile getProfile() {
        return getPrimaryKey().getProfile();
    }

    public void setProfile(Profile profile) {
        getPrimaryKey().setProfile(profile);
    }

    @Transient
    public Genre getGenre() {
        return getPrimaryKey().getGenre();
    }

    public void setGenre(Genre genre) {
        getPrimaryKey().setGenre(genre);
    }
}
