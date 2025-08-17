package robots.log;

public final class RobotModel {
    private static final RobotPositionSource robotPositionModel = new RobotPositionSource();

    private RobotModel() {}

    public static RobotPositionSource getRobotPositionModel() {
        return robotPositionModel;
    }
}
