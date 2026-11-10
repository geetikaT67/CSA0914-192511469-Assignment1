package parking.service;

import parking.model.ParkingSlot;
import parking.model.Vehicle;

public class ParkingService {
    private final ParkingSlot[][] facility;

    public ParkingService(int[] slotsPerFloor) {
        if (slotsPerFloor == null || slotsPerFloor.length == 0) {
            throw new IllegalArgumentException("Facility must have at least one floor.");
        }
        facility = new ParkingSlot[slotsPerFloor.length][];
        for (int f = 0; f < slotsPerFloor.length; f++) {
            facility[f] = new ParkingSlot[slotsPerFloor[f]];
            for (int s = 0; s < slotsPerFloor[f]; s++) {
                facility[f][s] = new ParkingSlot(f, s);
            }
        }
    }

    public ParkingSlot parkVehicle(Vehicle v) {
        if (v == null) return null;
        for (int f = 0; f < facility.length; f++) {
            for (int s = 0; s < facility[f].length; s++) {
                if (facility[f][s].isFree()) {
                    facility[f][s].occupy(v);
                    return facility[f][s];
                }
            }
        }
        return null;
    }

    public boolean vacateSlot(int floor, int slot) {
        if (floor < 0 || floor >= facility.length) return false;
        if (slot < 0 || slot >= facility[floor].length) return false;
        if (facility[floor][slot].isFree()) return false;
        facility[floor][slot].vacate();
        return true;
    }

    public int getAvailableSlotCount() {
        int count = 0;
        for (ParkingSlot[] floor : facility) {
            for (ParkingSlot slot : floor) {
                if (slot.isFree()) count++;
            }
        }
        return count;
    }
}
