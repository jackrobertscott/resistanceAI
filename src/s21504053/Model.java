package s21504053;

import cits3001_2016s2.*;

import java.util.*;

public class Model {
    private char name;

    private int selectsSuccessfulTeam;
    private int votesForSuccessfulTeam;
    private int votesForUnsuccessfulTeam;
    private int votesForTeamWithTwoSabotages;
    private int teamOnIsUnsuccessful;
    private int selectsTeamFeaturingSelf;
    private int votesForTeamFeaturingSelf;
    private int votesForTeamNotFeaturingSelf;
    private int votesForTeam3NotFeaturingSelf;
    private int votesAgainstTeamOnFifthAttempt;

    public Model(char name) {
        this.name = name;

        selectsSuccessfulTeam = 0;
        votesForSuccessfulTeam = 0;
        votesForUnsuccessfulTeam = 0;
        votesForTeamWithTwoSabotages = 0;
        teamOnIsUnsuccessful = 0;
        selectsTeamFeaturingSelf = 0;
        votesForTeamFeaturingSelf = 0;
        votesForTeamNotFeaturingSelf = 0;
        votesForTeam3NotFeaturingSelf = 0;
        votesAgainstTeamOnFifthAttempt = 0;
    }

    public int get_selectsSuccessfulTeam() {
        return selectsSuccessfulTeam;
    }

    public int increment_selectsSuccessfulTeam() {
        return ++selectsSuccessfulTeam;
    }

    public int get_votesForSuccessfulTeam() {
        return votesForSuccessfulTeam;
    }

    public int increment_votesForSuccessfulTeam() {
        return ++votesForSuccessfulTeam;
    }

    public int get_votesForUnsuccessfulTeam() {
        return votesForUnsuccessfulTeam;
    }

    public int increment_votesForUnsuccessfulTeam() {
        return ++votesForUnsuccessfulTeam;
    }

    public int get_votesForTeamWithTwoSabotages() {
        return votesForTeamWithTwoSabotages;
    }

    public int increment_votesForTeamWithTwoSabotages() {
        return ++votesForTeamWithTwoSabotages;
    }

    public int get_teamOnIsUnsuccessful() {
        return teamOnIsUnsuccessful;
    }

    public int increment_teamOnIsUnsuccessful() {
        return ++teamOnIsUnsuccessful;
    }

    public int get_selectsTeamFeaturingSelf() {
        return selectsTeamFeaturingSelf;
    }

    public int increment_selectsTeamFeaturingSelf() {
        return ++selectsTeamFeaturingSelf;
    }

    public int get_votesForTeamFeaturingSelf() {
        return votesForTeamFeaturingSelf;
    }

    public int increment_votesForTeamFeaturingSelf() {
        return ++votesForTeamFeaturingSelf;
    }

    public int get_votesForTeamNotFeaturingSelf() {
        return votesForTeamNotFeaturingSelf;
    }

    public int increment_votesForTeamNotFeaturingSelf() {
        return ++votesForTeamNotFeaturingSelf;
    }

    public int get_votesForTeam3NotFeaturingSelf() {
        return votesForTeam3NotFeaturingSelf;
    }

    public int increment_votesForTeam3NotFeaturingSelf() {
        return ++votesForTeam3NotFeaturingSelf;
    }

    public int get_votesAgainstTeamOnFifthAttempt() {
        return votesAgainstTeamOnFifthAttempt;
    }

    public int increment_votesAgainstTeamOnFifthAttempt() {
        return ++votesAgainstTeamOnFifthAttempt;
    }
}