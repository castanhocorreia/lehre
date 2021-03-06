package com.lehre.course.repository;

import com.lehre.course.domain.Course;
import com.lehre.course.domain.Lesson;
import com.lehre.course.domain.Module;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.UUID;

public class SpecTemplate {
    public static Specification<Module> modulesIntoCourse(final UUID courseId) {
        return (root, query, cb) -> {
            query.distinct(true);
            Root<Course> course = query.from(Course.class);
            Expression<Collection<Module>> modulesIntoCourse = course.get("modules");
            return cb.and(
                cb.equal(course.get("courseId"), courseId), cb.isMember(root, modulesIntoCourse));
        };
    }

    public static Specification<Lesson> lessonsIntoModule(final UUID moduleId) {
        return (root, query, cb) -> {
            query.distinct(true);
            Root<Module> module = query.from(Module.class);
            Expression<Collection<Lesson>> lessonsIntoCourse = module.get("lessons");
            return cb.and(
                cb.equal(module.get("moduleId"), moduleId), cb.isMember(root, lessonsIntoCourse));
        };
    }

    @And({
        @Spec(path = "level", spec = Equal.class),
        @Spec(path = "name", spec = Like.class),
        @Spec(path = "status", spec = Equal.class)
    })
    public interface CourseSpec extends Specification<Course> {
    }

    @Spec(path = "title", spec = Like.class)
    public interface ModuleSpec extends Specification<Module> {
    }

    @Spec(path = "title", spec = Like.class)
    public interface LessonSpec extends Specification<Lesson> {
    }
}
