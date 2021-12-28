package study.datajpa.reository;

public interface NestedClosedProjections {
    String getUsername();
    TeamInfo getTeam();
    interface TeamInfo{
        String getName();
    }
}
