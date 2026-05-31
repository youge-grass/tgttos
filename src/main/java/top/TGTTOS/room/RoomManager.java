package top.TGTTOS.room;

import java.util.ArrayList;
import java.util.List;

public class RoomManager {
    private static RoomManager instance;
    private final List<Room> roomList = new ArrayList<>();

    private RoomManager(){
        roomList.add(new Room(1));
        roomList.add(new Room(2));
        roomList.add(new Room(3));
    }

    public static RoomManager getInstance(){
        if(instance==null) instance = new RoomManager();
        return instance;
    }

    //IDE提示未使用，保留（后续新增房间快捷指令要用）
    public Room getRoom(int roomId){
        for(Room r : roomList){
            if(r.getId()==roomId) return r;
        }
        return null;
    }
    public List<Room> getRooms(){
        return roomList;
    }
}