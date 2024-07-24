package edu.escuelaing.arsw.ase.app.entity;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("users")
public class User implements Serializable {

    @Id
    private String id;

    private int score;

    private String name;

    public User() {
    }

    public User(String id, int score, String name) {
        this.id = id;
        this.score = score;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", score=" + score + ", name=" + name + "]";
    }

}
