package com.teachtouch.backend.guide.entity;
import com.teachtouch.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "guide_progress",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_guide", columnNames = {"user_id", "guide_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuideProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Guide guide;

    @Column(nullable = false)
    private boolean completed = false;
}

