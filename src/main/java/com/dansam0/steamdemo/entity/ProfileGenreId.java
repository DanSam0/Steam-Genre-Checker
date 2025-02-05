package com.dansam0.steamdemo.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(exclude="profile")
public class ProfileGenreId {

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Profile profile;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Genre genre;
}
