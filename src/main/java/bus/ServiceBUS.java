package bus;

import dao.ServiceDAO;
import entity.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ServiceBUS {

    private ServiceDAO serviceDAO = new ServiceDAO();

    public List<Service> getAll() {
        return serviceDAO.getAllServices();
    }

    public List<Service> getByType(String type) {
        type = cap(normalize(type));
        if (type.isEmpty()) return getAll();

        List<Service> list = serviceDAO.getAllServiceByType(type);
        return list != null ? list : new ArrayList<>();
    }

    public Service getByID(String id) {
        id = check(id);
        if (id.isEmpty()) return null;

        return serviceDAO.getServiceByID(id);
    }

    public Service getByName(String name)   {
        return serviceDAO.getByName(name);
    }

    // Thêm service mới (ko valid data truoc khi add)
    public boolean add(String name, String type, int qty, double price, File imageFile) {
        if (imageFile == null) throw new IllegalArgumentException("Vui lòng chọn ảnh dịch vụ.");
        String savedPath = saveImage(imageFile); // lưu ảnh
        Service s = new Service(check(name), cap(normalize(type)), qty, price, savedPath);
        validData(s);

        return serviceDAO.addService(s);
    }

    // valid data trước khi add
    public boolean add(Service s) {
        validData(s);
        return serviceDAO.addService(s);
    }

    // cập nhật thông tin service
    public boolean updateInfo(String serviceID, String newName, String serviceType, int quantity, double newPrice, String imgSource) {
        serviceID = check(serviceID);
        if (serviceID.isEmpty()) throw new IllegalArgumentException("Thiếu mã dịch vụ.");
        if (isBlank(newName)) throw new IllegalArgumentException("Tên dịch vụ không được để trống.");
        if (newPrice <= 0) throw new IllegalArgumentException("Giá phải > 0.");

        Service s = new Service(serviceID, check(newName), serviceType, quantity, newPrice, imgSource);
        return serviceDAO.updateInformationService(s);
    }

    // cập nhật số lượng mới
    public boolean updateQuantity(String serviceID, int newQty) {
        return serviceDAO.updateQuantityService(newQty, serviceID);
    }

    // lấy số luượng đã nhập (số lượng muốn thêm vào cho service) --> gọi increaseQuantity
    public boolean increaseQuantity(String serviceID, int add) {
        if (add <= 0) throw new IllegalArgumentException("Số lượng thêm phải > 0.");

        Service cur = getByID(serviceID);
        if (cur == null) throw new IllegalArgumentException("Không tìm thấy dịch vụ.");
        return updateQuantity(serviceID, cur.getQuantity() + add);
    }

    // xóa service
    public boolean delete(String serviceID) {
        serviceID = check(serviceID);
        if (serviceID.isEmpty()) throw new IllegalArgumentException("Thiếu mã dịch vụ.");

        return serviceDAO.removeServiceByID(serviceID);
    }

    // validData
    private static void validData(Service s) {
        if (s == null) throw new IllegalArgumentException("Thiếu dữ liệu dịch vụ.");
        if (isBlank(s.getServiceName())) throw new IllegalArgumentException("Tên dịch vụ không được để trống.");
        if (isBlank(s.getServiceType())) throw new IllegalArgumentException("Loại dịch vụ không được trống.");
        if (s.getPrice() <= 0) throw new IllegalArgumentException("Giá phải > 0.");
        if (s.getQuantity() < 0) throw new IllegalArgumentException("Số lượng phải >= 0.");
    }

    private static String saveImage(File file) {
        try {
            BufferedImage bi = ImageIO.read(file);
            String ext = ext(file.getName());
            File dir = new File("images");
            if (!dir.exists()) dir.mkdirs();
            String safeName = System.currentTimeMillis() + "_" + file.getName().replaceAll("[^a-zA-Z0-9._-]", "_");
            File out = new File(dir, safeName);
            ImageIO.write(bi, ext, out);
            return out.getPath().replace('\\','/');
        } catch (Exception ex) {
            throw new IllegalArgumentException("Không thể lưu ảnh: " + ex.getMessage());
        }
    }

    private static String ext(String name) {
        int i = name.lastIndexOf('.');
        return (i > 0 && i < name.length() - 1) ? name.substring(i + 1) : "png";
    }

    private static String check(String s){
        return s == null ? "" : s.trim();
    }

    private static String normalize(String s){
        return check(s).toLowerCase();
    }

    // Chữ đầu viết hoa
    private static String cap(String s){
        return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private static boolean isBlank(String s){
        return s == null || s.trim().isEmpty();
    }
}
