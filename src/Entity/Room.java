package Entity;

import java.util.Objects;

public class Room {
    private String roomID;
    private String description;
    private boolean isAvailable;
    private RoomType roomType;
    private String imgRoomSource; // thuộc tính mới

    public Room() {
    	
    }

    public Room(String roomID, String description, boolean isAvailable, RoomType roomType) {
        setRoomID(roomID);
        setDescription(description);
        setAvailable(isAvailable);
        setRoomType(roomType);
    }

    public Room(String roomID, String description, boolean isAvailable, RoomType roomType, String imgRoomSource) {
        this.roomID = roomID;
        this.description = description;
        this.isAvailable = isAvailable;
        this.roomType = roomType;
        this.imgRoomSource = imgRoomSource;
    }

    // Add Room
    public Room(String description, boolean isAvailable, RoomType roomType, String imgRoomSource) {
        this.description = description;
        this.isAvailable = isAvailable;
        this.roomType = roomType;
        this.imgRoomSource = imgRoomSource;
    }


    // -------------------------------------------------------------------------------------------------------------------
    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        if(roomID.trim().isEmpty()) throw new RuntimeException("Mã không được rỗng");
        this.roomID = roomID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null || description.trim().length() < 15) {
            throw new IllegalArgumentException("thông tin mô tả của phòng phải từ 15 kí tự trở lên");
        }
        this.description = description.trim();
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        if (roomType == null) {
            throw new IllegalArgumentException("Loại phòng không được rỗng");
        }
        this.roomType = roomType;
    }

    public String getImgRoomSource() {
        return imgRoomSource;
    }

    public void setImgRoomSource(String imgRoomSource) {
        this.imgRoomSource = imgRoomSource;
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomID);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Room other = (Room) obj;
        return Objects.equals(roomID, other.roomID);
    }

    public Object[] getObjects() {
        return new Object[]{roomID, roomType, description, isAvailable};
    }

    @Override
    public String toString() {
        return "Room [roomID=" + roomID + ", description=" + description + ", isAvailable=" + isAvailable
                + ", roomType=" + roomType + "]";
    }
}
