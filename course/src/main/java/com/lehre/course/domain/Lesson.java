package com.lehre.course.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@Builder
@Entity
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@RequiredArgsConstructor
@Setter
@Table(name = "tb_lessons")
@ToString
public class LessonModel implements Serializable {
  public static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID lessonId;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String description;

  private String videoUrl;

  @Column(nullable = false)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
  private LocalDateTime creationDate;

  @JoinColumn(name = "module_id")
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @ToString.Exclude
  private ModuleModel module;

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (object == null || Hibernate.getClass(this) != Hibernate.getClass(object)) return false;
    LessonModel lessonModel = (LessonModel) object;
    return lessonId != null && Objects.equals(lessonId, lessonModel.lessonId);
  }
}