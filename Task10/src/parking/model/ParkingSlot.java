package parking.model;

public class ParkingSlot {
    private final int floor;
    private final int slotNumber;
    private Vehicle parkedVehicle;

    public ParkingSlot(int floor, int slotNumber) {
        if (floor < 0 || slotNumber < 0) {
            throw new IllegalArgumentException("Floor and slot indices must be non-negative.");
        }
        this.floor = floor;
        this.slotNumber = slotNumber;
        this.parkedVehicle = null;
    }

    public int getFloor() { return floor; }
    public int getSlotNumber() { return slotNumber; }
    public boolean isFree() { return parkedVehicle == null; }
    public Vehicle getParkedVehicle() { return parkedVehicle; }

    public void occupy(Vehicle v) {
        if (v == null) {
            throw new IllegalArgumentException("Cannot occupy slot with null vehicle.");
        }
        if (this.parkedVehicle != null) {
            throw new IllegalStateException("Slot is already occupied by " + parkedVehicle.getRegNo());
        }
        this.parkedVehicle = v;
    }

    public void vacate() {
        this.parkedVehicle = null;
    }

    @Override
    public String toString() {
        return "Floor " + floor + "-Slot " + slotNumber + (isFree() ? " [FREE]" : " [" + parkedVehicle.getRegNo() + "]");
    }
}
