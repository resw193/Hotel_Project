CREATE DATABASE mimosa_hotel;
GO

USE mimosa_hotel;
GO

-- Sequence + Table
-- 1. EmployeeType (Loại nhân viên: Lễ tân, Quản lý)
CREATE SEQUENCE EmployeeTypeSequence
    START WITH 1
    INCREMENT BY 1;
GO

CREATE TABLE EmployeeType (
    typeID CHAR(6) PRIMARY KEY DEFAULT ('ET' + RIGHT('0000' + CAST(NEXT VALUE FOR EmployeeTypeSequence AS VARCHAR(4)), 4)),
    typeName NVARCHAR(50) NOT NULL,
    description NVARCHAR(200)
);
GO

-- 2. Employee (Nhân viên)
CREATE SEQUENCE EmployeeSequence
    START WITH 1
    INCREMENT BY 1;
GO

CREATE TABLE Employee (
    employeeID CHAR(6) PRIMARY KEY DEFAULT ('Emp' + RIGHT('000' + CAST(NEXT VALUE FOR EmployeeSequence AS VARCHAR(3)), 3)),
    fullName NVARCHAR(100) NOT NULL,
    phone CHAR(10) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL,
    employeeTypeID CHAR(6) NOT NULL,
	imgSource varchar(50), 
	gender BIT,
    CONSTRAINT FK_EmployeeType_Employee FOREIGN KEY (employeeTypeID) REFERENCES EmployeeType(typeID)
);
GO

-- 3. Account (Tài khoản đăng nhập)
CREATE SEQUENCE AccountSequence
    START WITH 1
    INCREMENT BY 1;
GO

CREATE TABLE Account (
    accountID CHAR(6) PRIMARY KEY DEFAULT ('Acc' + RIGHT('000' + CAST(NEXT VALUE FOR AccountSequence AS VARCHAR(3)), 3)),
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    employeeID CHAR(6) NOT NULL,
    isActive BIT DEFAULT 1,
    CONSTRAINT FK_Employee_Account FOREIGN KEY (employeeID) REFERENCES Employee(employeeID)
);
GO

-- 4. Customer (Khách hàng)
CREATE SEQUENCE CustomerSequence
    START WITH 1
    INCREMENT BY 1;
GO

CREATE TABLE Customer (
    customerID CHAR(10) PRIMARY KEY DEFAULT ('Cus' + RIGHT('000000' + CAST(NEXT VALUE FOR CustomerSequence AS VARCHAR(6)), 6)),
    fullName NVARCHAR(100) NOT NULL,
    phone CHAR(10) NOT NULL UNIQUE,
    email VARCHAR(100),
    regisDate SMALLDATETIME NOT NULL DEFAULT GETDATE(),
    idCard CHAR(20),
    loyaltyPoints INT DEFAULT 0
);
GO

-- 5. Promotion (Khuyến mãi)
CREATE SEQUENCE PromotionSequence
    START WITH 1
    INCREMENT BY 1;
GO

CREATE TABLE Promotion (
    promotionID CHAR(7) PRIMARY KEY DEFAULT ('Promo' + RIGHT('00' + CAST(NEXT VALUE FOR PromotionSequence AS VARCHAR(2)), 2)),
    promotionName NVARCHAR(100) NOT NULL,
    discount MONEY NOT NULL,
    startTime SMALLDATETIME NOT NULL,
    endTime SMALLDATETIME NOT NULL,
    quantity INT,
    CONSTRAINT CK_Promotion_EndTime CHECK (endTime > startTime)
);
GO

-- 6. RoomType (Loại phòng: Đơn, Đôi)
CREATE SEQUENCE RoomTypeSequence
    START WITH 1
    INCREMENT BY 1;
GO

CREATE TABLE RoomType (
    roomTypeID CHAR(6) PRIMARY KEY DEFAULT ('RT' + RIGHT('0000' + CAST(NEXT VALUE FOR RoomTypeSequence AS VARCHAR(4)), 4)),
    typeName NVARCHAR(50) NOT NULL,
    pricePerHour MONEY NOT NULL,
    pricePerNight MONEY NOT NULL,
    pricePerDay MONEY NOT NULL,
    lateFeePerHour MONEY NOT NULL
);
GO

-- 7. Room (Phòng)
CREATE SEQUENCE RoomSequence
    START WITH 1
    INCREMENT BY 1;
GO

CREATE TABLE Room (
    roomID CHAR(6) PRIMARY KEY DEFAULT ('Room' + RIGHT('00' + CAST(NEXT VALUE FOR RoomSequence AS VARCHAR(2)), 2)),
    description NVARCHAR(200),
    isAvailable BIT DEFAULT 1,
    roomTypeID CHAR(6) NOT NULL,
    imgRoomSource VARCHAR(40),
    CONSTRAINT FK_RoomType_Room FOREIGN KEY (roomTypeID) REFERENCES RoomType(roomTypeID)
);
GO

-- 8. Service (Dịch vụ phát sinh)
CREATE SEQUENCE ServiceSequence
    START WITH 1
    INCREMENT BY 1;
GO

CREATE TABLE Service (
    serviceID CHAR(6) PRIMARY KEY DEFAULT ('Serv' + RIGHT('00' + CAST(NEXT VALUE FOR ServiceSequence AS VARCHAR(2)), 2)),
    serviceName NVARCHAR(100) NOT NULL,
    price MONEY NOT NULL,
    serviceType NVARCHAR(200),
    quantity INT,
    imgSource VARCHAR(200)
);
GO

-- 9. Order (Hóa đơn tổng)
CREATE SEQUENCE OrderSequence
    START WITH 1
    INCREMENT BY 1;
GO

CREATE TABLE [Order] (
    orderID CHAR(8) PRIMARY KEY DEFAULT ('Ord' + RIGHT('00000' + CAST(NEXT VALUE FOR OrderSequence AS VARCHAR(5)), 5)),
    orderDate SMALLDATETIME NOT NULL DEFAULT GETDATE(),
    total MONEY NOT NULL DEFAULT 0,
    employeeID CHAR(6) NOT NULL,
    customerID CHAR(10) NOT NULL,
    promotionID CHAR(7),
    orderStatus NVARCHAR(20) NOT NULL DEFAULT N'Chưa thanh toán' CONSTRAINT CK_OrderStatus CHECK (orderStatus IN (N'Thanh toán', N'Chưa thanh toán')),
    CONSTRAINT FK_Employee_Order FOREIGN KEY (employeeID) REFERENCES Employee(employeeID),
    CONSTRAINT FK_Customer_Order FOREIGN KEY (customerID) REFERENCES Customer(customerID),
    CONSTRAINT FK_Promotion_Order FOREIGN KEY (promotionID) REFERENCES Promotion(promotionID)
);
GO

-- 10. OrderDetailRoom (Chi tiết đặt phòng - Liên kết với Room, gộp thuộc tính BookingRoom)
CREATE SEQUENCE OrderDetailRoomSequence
    START WITH 1
    INCREMENT BY 1;
GO

CREATE TABLE OrderDetailRoom (
    orderDetailRoomID CHAR(10) PRIMARY KEY DEFAULT ('ODR' + RIGHT('000000' + CAST(NEXT VALUE FOR OrderDetailRoomSequence AS VARCHAR(6)), 6)),
    orderID CHAR(8) NOT NULL,
    roomID CHAR(6) NOT NULL,
    roomFee MONEY NOT NULL DEFAULT 0,
    bookingDate SMALLDATETIME NOT NULL DEFAULT GETDATE(),
    checkInDate SMALLDATETIME,
    checkOutDate SMALLDATETIME,
    bookingType NVARCHAR(20) NOT NULL CONSTRAINT CK_BookingType CHECK (bookingType IN (N'Giờ', N'Đêm', N'Ngày')),
    status NVARCHAR(50) NOT NULL CONSTRAINT CK_OrderDetailRoomStatus CHECK (status IN (N'Đặt', N'Hủy', N'Check-in', N'Hoàn tất')),
    CONSTRAINT CK_CheckOutDate CHECK (checkOutDate IS NULL OR checkOutDate > checkInDate),
    CONSTRAINT FK_Order_OrderDetailRoom FOREIGN KEY (orderID) REFERENCES [Order](orderID) ON DELETE CASCADE,
    CONSTRAINT FK_Room_OrderDetailRoom FOREIGN KEY (roomID) REFERENCES Room(roomID)
);
GO

-- 11. OrderDetailService (Chi tiết dịch vụ phát sinh)
CREATE SEQUENCE OrderDetailServiceSequence
    START WITH 1
    INCREMENT BY 1;
GO

CREATE TABLE OrderDetailService (
    orderDetailID CHAR(10) PRIMARY KEY DEFAULT ('OD' + RIGHT('000000' + CAST(NEXT VALUE FOR OrderDetailServiceSequence AS VARCHAR(6)), 6)),
    orderID CHAR(8) NOT NULL,
    quantity INT NOT NULL,
    serviceFee MONEY NOT NULL,
    serviceID CHAR(6) NOT NULL,
	roomID CHAR(6) NULL,
    CONSTRAINT FK_Order_OrderDetailService FOREIGN KEY (orderID) REFERENCES [Order](orderID) ON DELETE CASCADE,
    CONSTRAINT FK_Service_OrderDetailService FOREIGN KEY (serviceID) REFERENCES Service(serviceID),
	CONSTRAINT FK_OrderDetailService_Room FOREIGN KEY (roomID) REFERENCES Room(roomID)
);
GO

-- Stored Procedures
-- Procedure: Thêm phòng mới
CREATE PROCEDURE sp_AddRoom
    @description NVARCHAR(200),
    @roomTypeID CHAR(6)
AS
BEGIN
    INSERT INTO Room (description, roomTypeID)
    VALUES (@description, @roomTypeID);
END;
GO

-- Procedure: Đặt phòng (tạo Order, rồi thêm OrderDetailRoom cho từng phòng)
CREATE PROCEDURE sp_BookRoom
    @fullName NVARCHAR(100),
    @phone CHAR(10),
    @email VARCHAR(100) = NULL,
    @idCard CHAR(20) = NULL,
    @roomID CHAR(6),
    @employeeID CHAR(6),
    @bookingDate SMALLDATETIME,
    @checkInDate SMALLDATETIME,
    @checkOutDate SMALLDATETIME,
    @bookingType NVARCHAR(20)
AS
BEGIN
    DECLARE @customerID CHAR(10), @orderID CHAR(8), @roomTypeID CHAR(6), @roomFee MONEY, @loyaltyPoints INT, @discount MONEY = 0, @promotionID CHAR(7) = NULL, @total MONEY;

    -- Kiểm tra khách hàng tồn tại bằng phone
    SELECT @customerID = customerID FROM Customer WHERE phone = @phone;

    -- Nếu chưa tồn tại, insert mới
    IF @customerID IS NULL BEGIN
        INSERT INTO Customer (fullName, phone, email, idCard)
        VALUES (@fullName, @phone, @email, @idCard);
        
        SET @customerID = (SELECT TOP 1 customerID FROM Customer ORDER BY customerID DESC);
    END;

    -- Kiểm tra xem khách hàng đã có Order đang hoạt động (Chưa thanh toán) chưa
    SELECT @orderID = orderID FROM [Order] WHERE customerID = @customerID AND orderStatus = N'Chưa thanh toán';

    -- Nếu chưa có Order, tạo mới với total = 0
    IF @orderID IS NULL 
	BEGIN
        INSERT INTO [Order] (orderDate, employeeID, customerID, total)
        VALUES (GETDATE(), @employeeID, @customerID, 0);
        
        SET @orderID = (SELECT TOP 1 orderID FROM [Order] ORDER BY orderID DESC);
    END;
    
    -- Tính roomFee cho phòng
    SELECT @roomTypeID = roomTypeID FROM Room WHERE roomID = @roomID;
    SET @roomFee = dbo.fn_CalculateRoomFee (@roomTypeID, @bookingType, @checkInDate, @checkOutDate);
    
    -- Thêm OrderDetailRoom
    INSERT INTO OrderDetailRoom (orderID, roomID, roomFee, bookingDate, checkInDate, checkOutDate, bookingType, status)
    VALUES (@orderID, @roomID, @roomFee, @bookingDate, @checkInDate, @checkOutDate, @bookingType, N'Đặt');
    
    -- Kiểm tra loyaltyPoints của customer để áp dụng khuyến mãi
    SELECT @loyaltyPoints = loyaltyPoints FROM Customer WHERE customerID = @customerID;
    
    IF @loyaltyPoints BETWEEN 10 AND 19 BEGIN
        SET @discount = 10;
        SET @promotionID = 'Promo01';  -- 10%
    END ELSE IF @loyaltyPoints BETWEEN 20 AND 39 BEGIN
        SET @discount = 15;
        SET @promotionID = 'Promo02';  -- 15%
    END ELSE IF @loyaltyPoints >= 40 BEGIN
        SET @discount = 20;
        SET @promotionID = 'Promo03';  -- 20%
    END;
    
	-- set khuyến mãi
    IF @discount > 0 BEGIN
        UPDATE [Order] SET promotionID = @promotionID WHERE orderID = @orderID;
    END;
    
    UPDATE Room SET isAvailable = 0 WHERE roomID = @roomID;
END;
GO

-- Procedure: Hủy đặt phòng (hủy OrderDetailRoom, nếu không còn chi tiết thì xóa Order) (hủy phòng khi phòng đó có status = 'Đặt' (tức là chưa check-in)
CREATE PROCEDURE sp_CancelBooking
    @roomID CHAR(6)
AS
BEGIN
    DECLARE @orderDetailRoomID CHAR(10), @orderID CHAR(8), @customerID CHAR(10);

    -- Tìm orderDetailRoomID từ roomID (giả định phòng đang có order hoạt động: status 'Đặt')
    SELECT TOP 1 @orderDetailRoomID = orderDetailRoomID, @orderID = orderID
    FROM OrderDetailRoom
    WHERE roomID = @roomID AND status = N'Đặt';

    IF @orderDetailRoomID IS NULL 
	BEGIN
        RAISERROR('Không tìm thấy đặt phòng cho phòng này hoặc đã check-in.', 16, 1);
        RETURN;
    END;

    -- Xóa OrderDetailRoom
    DELETE FROM OrderDetailRoom WHERE orderDetailRoomID = @orderDetailRoomID;

    -- Nếu Order không còn chi tiết phòng, xóa Order
    IF NOT EXISTS (SELECT 1 FROM OrderDetailRoom WHERE orderID = @orderID) 
	 BEGIN
        -- Lấy customerID trước khi xóa Order
        SELECT @customerID = customerID FROM [Order] WHERE orderID = @orderID;

        -- Xóa Order (OrderDetailService sẽ tự xóa theo FK ON DELETE CASCADE nếu có)
        DELETE FROM [Order] WHERE orderID = @orderID;

        -- Chỉ xóa Customer nếu KH không còn Order nào khác
        IF @customerID is not null AND not exists (SELECT 1 FROM [Order] WHERE customerID = @customerID)
        BEGIN
            DELETE FROM Customer WHERE customerID = @customerID;
        END
    END;

    -- Cập nhật isAvailable phòng
    UPDATE Room SET isAvailable = 1 WHERE roomID = @roomID;
END;
GO

-- Procedure: Check-in (cập nhật checkInDate trong OrderDetailRoom)
CREATE PROCEDURE sp_CheckIn
    @roomID CHAR(6)
AS
BEGIN
    DECLARE @orderDetailRoomID CHAR(10);

    -- Tìm orderDetailRoomID từ roomID (giả định phòng đang có order hoạt động: status 'Đặt')
    SELECT TOP 1 @orderDetailRoomID = orderDetailRoomID
    FROM OrderDetailRoom
    WHERE roomID = @roomID AND status = N'Đặt'

    IF @orderDetailRoomID IS NULL BEGIN
        RAISERROR('Không tìm thấy đặt phòng cho phòng này hoặc đã check-in.', 16, 1);
        RETURN;
    END;

    UPDATE OrderDetailRoom SET status = N'Check-in' WHERE orderDetailRoomID = @orderDetailRoomID;
END;
GO

-- Procedure: Check-out (cập nhật checkOutDate, tính lại roomFee nếu trễ, áp dụng khuyến mãi trên total)
CREATE OR ALTER PROCEDURE sp_CheckOut
    @roomID CHAR(6)
AS
BEGIN
    DECLARE @orderDetailRoomID CHAR(10), @currentTime SMALLDATETIME = GETDATE(), @roomTypeID CHAR(6), @bookingType NVARCHAR(20), @checkInDate SMALLDATETIME, @roomFee MONEY, @orderID CHAR(8), @total MONEY, @loyaltyPoints INT, @discount MONEY = 0, @promotionID CHAR(7) = NULL, @customerID CHAR(10);

    -- Tìm orderDetailRoomID từ roomID (giả định phòng đang có order hoạt động: status 'Check-in')
    SELECT TOP 1 @orderDetailRoomID = orderDetailRoomID, @orderID = orderID, @bookingType = bookingType, @checkInDate = checkInDate
    FROM OrderDetailRoom
    WHERE roomID = @roomID AND status = N'Check-in'

    IF @orderDetailRoomID IS NULL BEGIN
        RAISERROR('Không tìm thấy check-in cho phòng này hoặc đã check-out.', 16, 1);
        RETURN;
    END;

    SELECT @roomTypeID = roomTypeID FROM Room WHERE roomID = @roomID;

    SELECT @roomFee = roomFee FROM OrderDetailRoom WHERE orderDetailRoomID = @orderDetailRoomID;
    
    UPDATE OrderDetailRoom SET status = N'Hoàn tất' WHERE orderDetailRoomID = @orderDetailRoomID;

	-- Cộng roomFee vào total Order
    UPDATE [Order] SET total = total + ISNULL(@roomFee, 0) WHERE orderID = @orderID;

    -- Tăng loyaltyPoints 2 điểm (trước khi áp khuyến mãi)
    SELECT @customerID = customerID FROM [Order] WHERE orderID = @orderID;
    UPDATE Customer SET loyaltyPoints = loyaltyPoints + 2 WHERE customerID = @customerID;

    -- lấy lại tổng hiện tại để @total không bị NULL 
    SELECT @total = ISNULL(total, 0) FROM [Order] WHERE orderID = @orderID;

    -- Kiểm tra loyaltyPoints mới để áp dụng khuyến mãi
    SELECT @loyaltyPoints = loyaltyPoints FROM Customer WHERE customerID = @customerID;
    
    IF @loyaltyPoints BETWEEN 10 AND 19 BEGIN
        SET @discount = 10;
        SET @promotionID = 'Promo01';  -- 10%
    END ELSE IF @loyaltyPoints BETWEEN 20 AND 39 BEGIN
        SET @discount = 15;
        SET @promotionID = 'Promo02';  -- 15%
    END ELSE IF @loyaltyPoints >= 40 BEGIN
        SET @discount = 20;
        SET @promotionID = 'Promo03';  -- 20%
    END;
   
    -- Áp dụng khuyến mãi vào total (giảm % trên total hiện tại)
    IF @discount > 0 BEGIN
        UPDATE [Order] SET promotionID = @promotionID WHERE orderID = @orderID;
    END ELSE BEGIN
        UPDATE [Order] SET total = @total WHERE orderID = @orderID;
    END;

	-- Set isAvailable của room = 1 (trống)
    UPDATE Room SET isAvailable = 1 WHERE roomID = @roomID;
END;
GO

-- Gia hạn phòng (chỉ gia hạn khi status = 'Check-in' và thời gian check-out mới sau khi gia hạn phải > thời gian check-out cũ
-- Procedure: Gia hạn phòng (chỉ khi đang Check-in), cập nhật checkOutDate và tính lại roomFee
CREATE PROCEDURE sp_GiaHanPhong
    @roomID          CHAR(6),
    @newCheckOutDate SMALLDATETIME
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE 
        @orderDetailRoomID CHAR(10),
        @bookingType       NVARCHAR(20),
        @checkInDate       SMALLDATETIME,
        @oldCheckOutDate   SMALLDATETIME,
        @roomTypeID        CHAR(6);

    -- Lấy bản ghi OrderDetailRoom đang Check-in hiện tại kèm loại phòng (để tính roomFee mới sau khi thời gian check-out dài ra)
    SELECT TOP 1
           @orderDetailRoomID = odr.orderDetailRoomID,
           @bookingType       = odr.bookingType,
           @checkInDate       = odr.checkInDate,
           @oldCheckOutDate   = odr.checkOutDate,
           @roomTypeID        = r.roomTypeID
      FROM OrderDetailRoom odr
      JOIN Room r ON r.roomID = odr.roomID
     WHERE odr.roomID = @roomID
       AND odr.status = N'Check-in';

    IF @orderDetailRoomID IS NULL
    BEGIN
        RAISERROR(N'Chỉ gia hạn được phòng đang Check-in hoặc không tìm thấy đặt phòng.', 16, 1);
        RETURN;
    END;

    IF @newCheckOutDate IS NULL OR @newCheckOutDate <= @checkInDate
    BEGIN
        RAISERROR(N'Giờ trả phòng mới phải lớn hơn giờ nhận phòng.', 16, 1);
        RETURN;
    END;

    -- Nếu chỉ cho phép "gia hạn" (không rút ngắn)
    IF @oldCheckOutDate IS NOT NULL AND @newCheckOutDate < @oldCheckOutDate
    BEGIN
        RAISERROR(N'Giờ trả phòng mới không được sớm hơn giờ trả phòng hiện tại.', 16, 1);
        RETURN;
    END;

    -- Cập nhật thời gian trả phòng và tính lại phí
    UPDATE OrderDetailRoom
       SET checkOutDate = @newCheckOutDate,
           roomFee      = dbo.fn_CalculateRoomFee(@roomTypeID, @bookingType, @checkInDate, @newCheckOutDate)
     WHERE orderDetailRoomID = @orderDetailRoomID;

    -- Trả về thông tin đã cập nhật
    --SELECT orderDetailRoomID, orderID, roomID, bookingType, checkInDate, checkOutDate, roomFee, status
    --FROM OrderDetailRoom
    --WHERE orderDetailRoomID = @orderDetailRoomID;
END;
GO


-- Procedure: Thêm dịch vụ phát sinh
CREATE PROCEDURE sp_AddServiceDetail
    @orderID CHAR(8),
    @quantity INT,
    @serviceFee MONEY,
    @serviceID CHAR(6)
AS
BEGIN
    INSERT INTO OrderDetailService (orderID, quantity, serviceFee, serviceID)
    VALUES (@orderID, @quantity, @serviceFee, @serviceID);
END;
GO


-- Procedure: Thanh toán hóa đơn (cập nhật orderStatus = 'Thanh toán')
CREATE PROCEDURE sp_PayOrder
    @orderID CHAR(8)
AS
BEGIN
    SET NOCOUNT ON;

    IF NOT EXISTS (SELECT 1 FROM [Order] WHERE orderID = @orderID AND orderStatus = N'Chưa thanh toán')
    BEGIN
        RAISERROR(N'Hóa đơn không tồn tại hoặc đã thanh toán.', 16, 1);
        RETURN;
    END

    -- Xác lập NGÀY HÓA ĐƠN = lúc thanh toán
    UPDATE [Order]
       SET orderDate  = GETDATE(),
           orderStatus = N'Thanh toán'
     WHERE orderID = @orderID;

    -- Trả phòng
    UPDATE r
       SET r.isAvailable = 1
      FROM Room r
      JOIN OrderDetailRoom odr ON r.roomID = odr.roomID
     WHERE odr.orderID = @orderID;

    UPDATE OrderDetailRoom SET status = N'Hoàn tất' WHERE orderID = @orderID;

	-- Tính lại total sau khi áp dụng khuyến mãi để ra được thành tiền cuối cùng
	DECLARE @subTotal  MONEY, @discount  DECIMAL(9,2), @finalTotal MONEY;

    SELECT 
        @subTotal = ISNULL(o.total, 0),
        @discount = ISNULL(CAST(p.discount AS DECIMAL(9,2)), 0)
    FROM [Order] o
    LEFT JOIN Promotion p ON o.promotionID = p.promotionID
    WHERE o.orderID = @orderID;

	-- Check Order có khuyến mãi hay không
    IF @discount < 0 OR @discount > 100 SET @discount = 0;

	-- Tính ra thành tiền cuối cùng
    SET @finalTotal = CAST(@subTotal * (1 - (@discount / 100.0)) AS MONEY);

    UPDATE [Order]
    SET total = @finalTotal
    WHERE orderID = @orderID;

    -- Trả về gói thông tin để in 
	SELECT o.orderID, o.orderDate, o.employeeID, o.customerID, o.total, o.orderStatus,
           r.description, rt.roomTypeID, odr.bookingDate, odr.checkInDate, odr.checkOutDate, odr.bookingType, svc.serviceName, svc.serviceQuantity
     FROM [Order] o
    LEFT JOIN OrderDetailRoom odr ON o.orderID = odr.orderID
    LEFT JOIN Room r  ON odr.roomID = r.roomID
    LEFT JOIN RoomType rt ON r.roomTypeID = rt.roomTypeID
    OUTER APPLY (
        SELECT s.serviceName,
               SUM(ods.quantity) AS serviceQuantity
        FROM OrderDetailService ods
        JOIN Service s ON s.serviceID = ods.serviceID
        WHERE ods.orderID = o.orderID AND ods.roomID = odr.roomID
		GROUP BY s.serviceName
    ) AS svc
    WHERE o.orderID = @orderID
    ORDER BY odr.roomID, svc.serviceName;
END;
GO

-- Procedure: Cập nhật dịch vụ cho phòng (thêm dịch vụ vào OrderDetailService, cập nhật total Order)
-- Chức năng cập nhật dịch vụ cho phòng đang sử dụng (Khi phòng đã check-in thì mới được thêm dịch vụ vào phòng)
-- Sử dụng trong Quản lý phòng (mỗi dòng trong table là 1 phòng --> khi click chuột phải vào có nút cập nhật dịch vụ)
-- Khi cập nhật dịch vụ cho phòng thì --> insert into OrderDetailService và cộng tiền Service (price * quantity (vừa đặt)) vào Total trong Order (không tính lại roomFee)
CREATE PROCEDURE sp_AddServiceToRoom
    @roomID CHAR(6),
    @serviceName NVARCHAR(100),
    @quantity INT
AS
BEGIN
    DECLARE @orderID CHAR(8),
			@serviceID CHAR(6),
			@price MONEY,
			@serviceFee MONEY,
			@stock INT;

    -- Tìm orderID từ roomID (giả định room đang có order hoạt động: status 'Đặt' hoặc 'Check-in')
    SELECT TOP 1 @orderID = odr.orderID
    FROM OrderDetailRoom odr
    WHERE odr.roomID = @roomID AND odr.status = 'Check-in'

    IF @orderID IS NULL BEGIN
        RAISERROR('Không tìm thấy hóa đơn cho phòng này hoặc phòng không hoạt động.', 16, 1);
        RETURN;
    END;

    -- Tìm serviceID và price từ serviceName
    SELECT @serviceID = s.serviceID,
           @price     = s.price,
           @stock     = s.quantity
      FROM Service s WITH (ROWLOCK, UPDLOCK)
     WHERE s.serviceName = @serviceName;

	-- check serviceID
    IF @serviceID IS NULL 
	BEGIN
        RAISERROR('Dịch vụ không tồn tại.', 16, 1);
        RETURN;
    END;

	-- check tồn kho (quantity trong service hiện tại)
	IF @stock IS NULL OR @stock < @quantity
    BEGIN
		DECLARE @remain INT = ISNULL(@stock, 0);
        RAISERROR(N'Số lượng dịch vụ trong kho không đủ. Còn %d.', 16, 1, @remain);
        RETURN;
    END;

    -- Tính serviceFee
    SET @serviceFee = @quantity * @price;

    BEGIN TRY
        BEGIN TRANSACTION;

            INSERT INTO OrderDetailService (orderID, quantity, serviceFee, serviceID, roomID)
            VALUES (@orderID, @quantity, @serviceFee, @serviceID, @roomID);

            -- Cộng dồn serviceFee vào tổng hóa đơn ngay khi thêm dịch vụ
            UPDATE [Order]
               SET total = total + ISNULL(@serviceFee, 0)
             WHERE orderID = @orderID;

            -- Trừ kho (số lượng hiện có - số lượng vừa thêm)
            UPDATE Service
               SET quantity = quantity - @quantity
             WHERE serviceID = @serviceID;

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        DECLARE @msg NVARCHAR(4000) = ERROR_MESSAGE();
        RAISERROR(@msg, 16, 1);
    END CATCH
END;
GO

-- Procedure: Kết toán ca (thống kê số lượng hóa đơn theo ngày)
CREATE PROCEDURE sp_DailyOrderStats
    @date SMALLDATETIME
AS
BEGIN
    SELECT COUNT(*) AS soLuongHoaDon, SUM(total) AS TotalRevenue
    FROM [Order]
    WHERE CAST(orderDate AS DATE) = CAST(@date AS DATE) AND orderStatus = N'Thanh toán';
END;
GO

-- Thống kê số lượng hóa đơn kèm theo tổng thu nhập
CREATE PROCEDURE sp_DailyOrderStats
    @date SMALLDATETIME
AS
BEGIN
    SELECT COUNT(*) AS soLuongHoaDon, SUM(total) AS TotalRevenue
    FROM [Order]
    WHERE CAST(orderDate AS DATE) = CAST(@date AS DATE) AND orderStatus = N'Thanh toán';
END;
GO

-- Thống kê số lượng hóa đơn | Thu nhập phòng | Thu nhập dịch vụ | Tổng thu nhập
CREATE sp_DailyOrderStats_Detail
    @date SMALLDATETIME
AS
BEGIN
    ;WITH Paid AS (
        SELECT o.orderID, o.total
        FROM [Order] o
        WHERE CAST(o.orderDate AS DATE) = CAST(@date AS DATE)
          AND o.orderStatus = N'Thanh toán'
    )
    SELECT
        COUNT(*)                           AS soLuongHoaDon,
        SUM(p.total)                       AS TotalRevenue,
        (SELECT SUM(ISNULL(odr.roomFee,0))
           FROM OrderDetailRoom odr
           JOIN Paid p2 ON p2.orderID = odr.orderID)  AS RoomRevenue,
        (SELECT SUM(ISNULL(ods.serviceFee,0))
           FROM OrderDetailService ods
           JOIN Paid p3 ON p3.orderID = ods.orderID)  AS ServiceRevenue
    FROM Paid p;
END


-- Procedure: Thống kê dịch vụ đã sử dụng theo ngày
CREATE PROCEDURE sp_DailyServiceStats
    @date SMALLDATETIME
AS
BEGIN
    SELECT s.serviceName, SUM(od.quantity) AS TotalQuantity, SUM(od.serviceFee) AS TotalRevenue
    FROM OrderDetailService od
    JOIN Service s ON od.serviceID = s.serviceID
    JOIN [Order] o ON od.orderID = o.orderID
    WHERE CAST(o.orderDate AS DATE) = CAST(@date AS DATE)
    GROUP BY s.serviceName;
END;
GO

-- Thống kê doanh thu phòng theo loại đặt ('Giờ', 'Ngày', 'Đêm') trong startTime -> endTime
CREATE PROCEDURE sp_BookingTypeRevenueStats
    @start SMALLDATETIME,
    @end   SMALLDATETIME
AS
BEGIN
    SET NOCOUNT ON;

    SELECT  odr.bookingType,
            COUNT(*)                          AS SoLuot,
            SUM(ISNULL(odr.roomFee,0))        AS RoomRevenue
    FROM OrderDetailRoom odr
    JOIN [Order] o ON o.orderID = odr.orderID
    WHERE o.orderStatus = N'Thanh toán'
    AND o.orderDate BETWEEN @start AND @end
    GROUP BY odr.bookingType
    ORDER BY RoomRevenue DESC;
END
GO





-- Functions
-- Function: Tính phí phòng dựa trên loại (giờ, đêm, ngày) và thời gian
CREATE FUNCTION fn_CalculateRoomFee (
    @roomTypeID CHAR(6),
    @bookingType NVARCHAR(20),
    @checkInDate SMALLDATETIME,
    @checkOutDate SMALLDATETIME
) RETURNS MONEY
AS
BEGIN
    DECLARE @duration INT, @price MONEY = 0, @lateFee MONEY = 0, @hourlyPrice MONEY, @hourlyIncrement MONEY, @lateFeeRate MONEY, @typeName NVARCHAR(50);

    SET @duration = DATEDIFF(HOUR, @checkInDate, @checkOutDate);

    SELECT @hourlyPrice = pricePerHour, @lateFeeRate = lateFeePerHour, @typeName = typeName FROM RoomType WHERE roomTypeID = @roomTypeID;

    IF @bookingType = N'Giờ' BEGIN
        SET @hourlyIncrement = CASE WHEN @typeName = N'Phòng đơn' THEN 10000 ELSE 20000 END;
        SET @price = @hourlyPrice + (@duration - 1) * @hourlyIncrement;
    END ELSE IF @bookingType = N'Đêm' BEGIN
        SELECT @price = pricePerNight FROM RoomType WHERE roomTypeID = @roomTypeID;
        IF @duration > 13 SET @lateFee = (@duration - 13) * CASE WHEN @typeName = N'Phòng đơn' THEN 20000 ELSE 20000 END;
        SET @price = @price + @lateFee;
    END ELSE IF @bookingType = N'Ngày' BEGIN
        SELECT @price = pricePerDay FROM RoomType WHERE roomTypeID = @roomTypeID;
        IF @duration > 24 SET @lateFee = (@duration - 24) * CASE WHEN @typeName = N'Phòng đơn' THEN 20000 ELSE 30000 END;
        SET @price = @price + @lateFee;
    END;

    RETURN @price;
END;
GO

-- Thống kê
-- Function: Thống kê doanh thu theo thời gian
CREATE FUNCTION fn_RevenueStats (
    @startDate SMALLDATETIME,
    @endDate SMALLDATETIME
) RETURNS TABLE
AS
RETURN (
    SELECT SUM(total) AS TotalRevenue
    FROM [Order]
    WHERE orderDate BETWEEN @startDate AND @endDate AND orderStatus = N'Thanh toán'
);
GO

-- Function: Tỷ lệ lấp đầy phòng theo ngày
CREATE FUNCTION fn_OccupancyRate (
    @date SMALLDATETIME
) RETURNS DECIMAL(5,2)
AS
BEGIN
    DECLARE @totalRooms INT, @occupiedRooms INT;
    SELECT @totalRooms = COUNT(*) FROM Room;
    SELECT @occupiedRooms = COUNT(*) FROM OrderDetailRoom WHERE @date BETWEEN checkInDate AND checkOutDate AND checkOutDate IS NOT NULL;
    RETURN (CAST(@occupiedRooms AS DECIMAL) / @totalRooms) * 100;
END;
GO

-- Thống kê
-- Function: Thống kê dịch vụ phát sinh theo thời gian
CREATE FUNCTION fn_ServiceStats (
    @startDate SMALLDATETIME,
    @endDate SMALLDATETIME
) RETURNS TABLE
AS
RETURN (
    SELECT s.serviceName, SUM(od.quantity) AS TotalQuantity, SUM(od.serviceFee) AS TotalRevenue
    FROM OrderDetailService od
    JOIN Service s ON od.serviceID = s.serviceID
    JOIN [Order] o ON od.orderID = o.orderID
    WHERE o.orderDate BETWEEN @startDate AND @endDate
    GROUP BY s.serviceName
);
GO

-- Function: Thống kê số lần khách quay lại
CREATE FUNCTION fn_CustomerReturnStats (
    @customerID CHAR(10)
) RETURNS INT
AS
BEGIN
    RETURN (SELECT COUNT(*) FROM OrderDetailRoom WHERE orderID IN (SELECT orderID FROM [Order] WHERE customerID = @customerID) AND checkOutDate IS NOT NULL);
END;
GO

-- Triggers
-- Trigger: Cập nhật isAvailable khi update OrderDetailRoom (check-in/out)
--CREATE TRIGGER tr_UpdateRoomAvailability
--ON OrderDetailRoom
--AFTER UPDATE
--AS
--BEGIN
--    DECLARE @roomID CHAR(6);
--    SELECT @roomID = roomID FROM inserted;
    
--    IF EXISTS (SELECT * FROM inserted WHERE checkInDate IS NOT NULL AND checkOutDate IS NULL)
--        UPDATE Room SET isAvailable = 0 WHERE roomID = @roomID;
--    ELSE IF EXISTS (SELECT * FROM inserted WHERE checkOutDate IS NOT NULL)
--        UPDATE Room SET isAvailable = 1 WHERE roomID = @roomID;
--END;
--GO

-- Trigger: Tăng loyaltyPoints khi hoàn tất (checkOutDate not null)
--CREATE TRIGGER tr_UpdateLoyaltyOnComplete
--ON OrderDetailRoom
--AFTER UPDATE
--AS
--BEGIN
--    IF UPDATE(checkOutDate) AND EXISTS (SELECT * FROM inserted WHERE checkOutDate IS NOT NULL)
--    BEGIN
--        DECLARE @orderID CHAR(8), @customerID CHAR(10);
--        SELECT @orderID = orderID FROM inserted;
--        SELECT @customerID = customerID FROM [Order] WHERE orderID = @orderID;
--        UPDATE Customer
--        SET loyaltyPoints = loyaltyPoints + 2
--        WHERE customerID = @customerID;
--    END;
--END;
--GO

-- Trigger: Tính serviceFee OrderDetailService
CREATE TRIGGER tr_CalculateDetailServiceFee
ON OrderDetailService
AFTER INSERT, UPDATE
AS
BEGIN
    UPDATE od
    SET od.serviceFee = od.quantity * s.price
    FROM OrderDetailService od
    INNER JOIN inserted i ON od.orderDetailID = i.orderDetailID
    INNER JOIN Service s ON i.serviceID = s.serviceID;
END;
GO

-- Trigger: Cập nhật total Order khi thêm/cập nhật OrderDetailService hoặc OrderDetailRoom
--CREATE TRIGGER tr_UpdateOrderTotal
--ON OrderDetailRoom
--AFTER INSERT, UPDATE, DELETE
--AS
--BEGIN
--    DECLARE @orderID CHAR(8), @roomFee MONEY, @serviceFee MONEY, @total MONEY, @discount MONEY = 0, @loyaltyPoints INT, @promotionID CHAR(7), @customerID CHAR(10);

--    IF EXISTS (SELECT * FROM inserted)
--        SELECT @orderID = orderID FROM inserted;
--    ELSE IF EXISTS (SELECT * FROM deleted)
--        SELECT @orderID = orderID FROM deleted;

--    SET @roomFee = ISNULL((SELECT SUM(roomFee) FROM OrderDetailRoom WHERE orderID = @orderID), 0);
--    SET @serviceFee = ISNULL((SELECT SUM(serviceFee) FROM OrderDetailService WHERE orderID = @orderID), 0);
    
--    SET @total = @roomFee + @serviceFee;
    
--    -- Kiểm tra loyaltyPoints để áp dụng khuyến mãi
--    SELECT @customerID = customerID FROM [Order] WHERE orderID = @orderID;
--    SELECT @loyaltyPoints = loyaltyPoints FROM Customer WHERE customerID = @customerID;
    
--    IF @loyaltyPoints BETWEEN 10 AND 19 BEGIN
--        SET @discount = 10;
--        SET @promotionID = 'Promo01';  -- 10%
--    END ELSE IF @loyaltyPoints BETWEEN 20 AND 39 BEGIN
--        SET @discount = 15;
--        SET @promotionID = 'Promo02';  -- 15%
--    END ELSE IF @loyaltyPoints >= 40 BEGIN
--        SET @discount = 20;
--        SET @promotionID = 'Promo03';  -- 20%
--    END;
    
--    -- Áp dụng giảm giá trên total
--    IF @discount > 0 BEGIN
--        SET @total = @total * (1 - @discount / 100);
--        UPDATE [Order] SET promotionID = @promotionID WHERE orderID = @orderID;
--    END;
    
--    UPDATE [Order]
--    SET total = @total
--    WHERE orderID = @orderID;
--END;
--GO

--CREATE TRIGGER tr_UpdateOrderTotalFromService
--ON OrderDetailService
--AFTER INSERT, UPDATE, DELETE
--AS
--BEGIN
--    DECLARE @orderID CHAR(8), @roomFee MONEY, @serviceFee MONEY, @total MONEY, @discount MONEY = 0, @loyaltyPoints INT, @promotionID CHAR(7), @customerID CHAR(10);

--    IF EXISTS (SELECT * FROM inserted)
--        SELECT @orderID = orderID FROM inserted;
--    ELSE IF EXISTS (SELECT * FROM deleted)
--        SELECT @orderID = orderID FROM deleted;

--    SET @roomFee = ISNULL((SELECT SUM(roomFee) FROM OrderDetailRoom WHERE orderID = @orderID), 0);
--    SET @serviceFee = ISNULL((SELECT SUM(serviceFee) FROM OrderDetailService WHERE orderID = @orderID), 0);
    
--    SET @total = @roomFee + @serviceFee;
    
--    -- Kiểm tra loyaltyPoints để áp dụng khuyến mãi
--    SELECT @customerID = customerID FROM [Order] WHERE orderID = @orderID;
--    SELECT @loyaltyPoints = loyaltyPoints FROM Customer WHERE customerID = @customerID;
    
--    IF @loyaltyPoints BETWEEN 10 AND 19 BEGIN
--        SET @discount = 10;
--        SET @promotionID = 'Promo01';  -- 10%
--    END ELSE IF @loyaltyPoints BETWEEN 20 AND 39 BEGIN
--        SET @discount = 15;
--        SET @promotionID = 'Promo02';  -- 15%
--    END ELSE IF @loyaltyPoints >= 40 BEGIN
--        SET @discount = 20;
--        SET @promotionID = 'Promo03';  -- 20%
--    END;
    
    -- Áp dụng giảm giá trên total
--    IF @discount > 0 BEGIN
--        SET @total = @total * (1 - @discount / 100);
--        UPDATE [Order] SET promotionID = @promotionID WHERE orderID = @orderID;
--    END;
    
--    UPDATE [Order]
--    SET total = @total
--    WHERE orderID = @orderID;
--END;
--GO

-- Insert Dữ Liệu Mẫu
-- EmployeeType
INSERT INTO EmployeeType (typeName) VALUES (N'Lễ tân');
INSERT INTO EmployeeType (typeName) VALUES (N'Quản lý');


-- Employee
INSERT INTO Employee (fullName, phone, email, employeeTypeID) VALUES (N'Nguyễn Bảo Định', '0123456789', 'bao.dinh@example.com', 'ET0001');
INSERT INTO Employee (fullName, phone, email, employeeTypeID, imgSource, gender) VALUES (N'Sơn Tùng MTP', '0987654321', 'baodinh.nguyen321@gmail.com', 'ET0002', 'images/fox_profile.png', 1); -- Lễ tân
INSERT INTO Employee (fullName, phone, email, employeeTypeID) VALUES (N'Quang Hùng masterD', '0905112233', 'quanghungmasterd@mimosahotel.com', 'ET0001'); -- Lễ tân
INSERT INTO Employee (fullName, phone, email, employeeTypeID) VALUES (N'Hoàng Ngọc Hải', '0934556677', 'hai.hn@mimosahotel.com', 'ET0002'); -- Quản lý
GO


-- Account
INSERT INTO Account (username, password, employeeID) VALUES ('sa', 'hashed_password', 'Emp001');
INSERT INTO Account (username, password, employeeID) VALUES ('admin', '123', 'Emp002');

select * from Account
select * from Employee

-- RoomType
INSERT INTO RoomType (typeName, pricePerHour, pricePerNight, pricePerDay, lateFeePerHour) VALUES (N'Phòng đơn', 60000, 150000, 200000, 20000);
INSERT INTO RoomType (typeName, pricePerHour, pricePerNight, pricePerDay, lateFeePerHour) VALUES (N'Phòng đôi', 70000, 200000, 300000, 30000);

-- Room
INSERT INTO Room (description, roomTypeID) VALUES (N'Phòng đơn tầng 1', 'RT0001');
INSERT INTO Room (description, isAvailable, roomTypeID, imgRoomSource) VALUES (N'Phòng 101 - View vườn, gần sảnh', 1, 'RT0001', 'images/1.jpg');
INSERT INTO Room (description, isAvailable, roomTypeID, imgRoomSource) VALUES (N'Phòng 102 - View thành phố', 1, 'RT0001', 'images/2.jpg');
INSERT INTO Room (description, isAvailable, roomTypeID, imgRoomSource) VALUES (N'Phòng 103 - Yên tĩnh, cuối hành lang', 1, 'RT0001', 'images/3.jpg');
INSERT INTO Room (description, isAvailable, roomTypeID, imgRoomSource) VALUES (N'Phòng 201 - Ban công, view biển', 1, 'RT0002', 'images/4.jpg');
INSERT INTO Room (description, isAvailable, roomTypeID, imgRoomSource) VALUES (N'Phòng 202 - Ban công, view biển', 1, 'RT0002', 'images/5.jpg');
INSERT INTO Room (description, isAvailable, roomTypeID, imgRoomSource) VALUES (N'Phòng 203 - Gần thang máy', 1, 'RT0002', 'images/6.jpg');
GO

use mimosa_hotel


-- Service
INSERT INTO Service (serviceName, price, quantity, serviceType, imgSource) VALUES (N'Nước ngọt', 15000, 10, 'Drink', 'images/pepsi.png');
INSERT INTO Service (serviceName, price, quantity, serviceType, imgSource) VALUES (N'Nước uống đóng chai', 15000, 20, 'Drink', 'images/aquafina.jpg');
INSERT INTO Service (serviceName, price, quantity, serviceType, imgSource) VALUES (N'Mì tôm', 15000, 30, 'Food', 'images/mitom.jpg');
INSERT INTO Service (serviceName, price, quantity, serviceType, imgSource) VALUES (N'Cocacola', 15000, 10, 'Drink', 'images/cocacola.jpg');
INSERT INTO Service (serviceName, price, quantity, serviceType, imgSource) VALUES (N'Bia Tiger', 20000, 60, 'Drink', 'images/biatiger.jpg');
INSERT INTO Service (serviceName, price, quantity, serviceType, imgSource) VALUES (N'7 up', 20000, 60, 'Drink', 'images/7up.png');
INSERT INTO Service (serviceName, price, quantity, serviceType, imgSource) VALUES (N'Cơm bò xào', 30000, 60, 'Food', 'images/comboxao.jpg');
INSERT INTO Service (serviceName, price, quantity, serviceType, imgSource) VALUES (N'Mì ý', 30000, 60, 'Food', 'images/miy.jpg');
INSERT INTO Service (serviceName, price, quantity, serviceType, imgSource) VALUES (N'Nước ép táo', 20000, 60, 'Drink', 'images/nuoceptao.png');
INSERT INTO Service (serviceName, price, quantity, serviceType, imgSource) VALUES (N'Mì khoai tây omachi', 30000, 60, 'Food', 'images/omachi.png');
INSERT INTO Service (serviceName, price, quantity, serviceType, imgSource) VALUES (N'Redbull', 20000, 60, 'Drink', 'images/redbull.png');
INSERT INTO Service (serviceName, price, quantity, serviceType, imgSource) VALUES (N'Rượu vang', 20000, 60, 'Drink', 'images/ruouvang.png');
INSERT INTO Service (serviceName, price, quantity, serviceType, imgSource) VALUES (N'Soju', 20000, 60, 'Drink', 'images/soju.png');
INSERT INTO Service (serviceName, price, quantity, serviceType, imgSource) VALUES (N'Giặt là', 20000, 60, 'Laundry', 'images/laundry1.jpg');
INSERT INTO Service (serviceName, price, quantity, serviceType, imgSource) VALUES (N'Xếp khăn', 20000, 60, 'Laundry', 'images/xepkhan.jpg');
INSERT INTO Service (serviceName, price, quantity, serviceType, imgSource) VALUES (N'Sấy khô', 20000, 60, 'Laundry', 'images/saykho.jpg');
INSERT INTO Service (serviceName, price, quantity, serviceType, imgSource) VALUES (N'Dọn phòng', 20000, 60, 'Laundry', 'images/donphong.jpg');
INSERT INTO Service (serviceName, price, quantity, serviceType, imgSource) VALUES (N'Thu gom đồ bẩn', 20000, 60, 'Laundry', 'images/thugomdoban.jpg');


GO

-- Promotion
INSERT INTO Promotion (promotionName, discount, startTime, endTime, quantity) VALUES (N'Ưu đãi Bạc 10%', 10.00, '2025-01-01', '2026-12-31', 20);
INSERT INTO Promotion (promotionName, discount, startTime, endTime, quantity) VALUES (N'Ưu đãi Vàng 15%', 15.00, '2025-01-01', '2026-12-31', 20);
INSERT INTO Promotion (promotionName, discount, startTime, endTime, quantity) VALUES (N'Ưu đãi Kim Cương 20%', 20.00, '2025-01-01', '2026-12-31', 20);
GO

-- Customer
--INSERT INTO Customer (fullName, phone) VALUES (N'Khách Mẫu', '0987654321');
--INSERT INTO Customer (fullName, phone, email, idCard, loyaltyPoints) VALUES (N'Lê Văn Cường', '0903456789', 'cuong.le@email.com', '034567890123', 18); -- Sẽ được giảm 10%
--INSERT INTO Customer (fullName, phone, email, idCard, loyaltyPoints) VALUES (N'Phạm Thị Dung', '0904567890', 'dung.pham@email.com', '045678901234', 8); -- Không được giảm giá
--INSERT INTO Customer (fullName, phone, email, idCard, loyaltyPoints) VALUES (N'Vũ Thị Giang', '0906789012', 'giang.vu@email.com', '067890123456', 35); -- Sẽ được giảm 15%
--INSERT INTO Customer (fullName, phone, email, idCard, loyaltyPoints) VALUES (N'Nguyễn Văn An', '0901234567', 'an.nguyen@email.com', '012345678901', 42); -- Sẽ được giảm 20%
--GO

---- Order (Mẫu, khi đặt phòng) 
--EXEC sp_BookRoom N'Khách Mẫu', '0987654321', NULL, NULL, 'Room01', 'Emp001', '2025-09-01 00:00:00', '2025-09-01 14:00:00', '2025-09-02 12:00:00', N'Giờ';

---- Kịch bản 2: Lê Văn Cường (18 điểm) đặt phòng đôi theo đêm, sẽ được giảm giá 10%
--EXEC sp_BookRoom N'Lê Văn Cường', '0903456789', 'cuong.le@email.com', '034567890123', 'Room04', 'Emp002', '2025-09-15 00:00:00', '2025-09-20 19:00:00', '2025-09-21 08:00:00', N'Đêm';

---- Kịch bản 3: Phạm Thị Dung (8 điểm) đặt phòng đơn theo ngày, không giảm giá
--EXEC sp_BookRoom N'Phạm Thị Dung', '0904567890', 'dung.pham@email.com', '045678901234', 'Room02',  'Emp003',  '2025-10-09 00:00:00', '2025-10-15 11:00:00', '2025-10-16 11:00:00', N'Ngày';

---- Kịch bản 4: Vũ Thị Giang (35 điểm) đặt phòng đôi theo giờ, sẽ được giảm giá 15%
--EXEC sp_BookRoom N'Vũ Thị Giang', '0906789012', 'giang.vu@email.com', '067890123456',  'Room05', 'Emp002', '2025-10-01 00:00:00',  '2025-10-02 14:00:00', '2025-10-02 18:00:00',  N'Giờ';

---- Kịch bản 5: Nguyễn Văn An (42 điểm) đặt phòng đơn theo ngày, sẽ được giảm giá 20%
--EXEC sp_BookRoom N'Nguyễn Văn An', '0901234567', 'an.nguyen@email.com', '012345678901',  'Room03', 'Emp003',  '2025-10-08 00:00:00',  '2025-10-09 10:00:00',  '2025-10-10 10:00:00',  N'Ngày';
--GO

---- Kịch bản 6 : Phạm Thị Dung đặt phòng Room06 và thêm chi tiết hóa đơn thứ 2 vào Order tương ứng
--EXEC sp_BookRoom N'Phạm Thị Dung', '0904567890', 'dung.pham@email.com', '045678901234', 'Room06',  'Emp003',  '2025-10-09 00:00:00', '2025-10-16 11:00:00', '2025-10-17 11:00:00', N'Ngày';

-- Thống kê dịch vụ đã sử dụng
-- EXEC sp_DailyServiceStats '2025-10-09 10:00:00' 

-- Thống kê theo dịch vụ đã phát sinh theo thời gian bắt đầu -> kết thúc --> Sẽ có nhiều dịch vụ | Số lượng | Tổng thu nhập
-- select * from fn_ServiceStats ('2025-08-10', '2025-09-10')


-- chỉnh sửa thêm cột cho Table Room
--alter table Room
--add imgRoomSource varchar(40)

-- Chỉnh sửa thêm cột cho Table Service
--alter table Service
--add quantity int
--alter table Service 
--add imgSource varchar(40)

-- Chỉnh sửa thêm cột cho Table Promotion 
--alter table Promotion
--add quantity int

--alter table Employee
--add imgSource varchar(50)

--alter table Employee
--add gender bit

--ALTER TABLE OrderDetailService
--ADD roomID CHAR(6) NULL;

--ALTER TABLE OrderDetailService
--ADD CONSTRAINT FK_OrderDetailService_Room
--FOREIGN KEY (roomID) REFERENCES Room(roomID);

use mimosa_hotel
select * from Customer
select * from Service 
select * from Promotion	
select * from RoomType
select * from Room
select * from [Order] where orderStatus = N'Thanh toán'
select * from OrderDetailRoom
select * from OrderDetailService 
select * from Account where username = 'admin'
select * from Employee where employeeID = 'Emp002' -- sontungmtp@mimosahotel.com
select * from EmployeeType where typeID = 'ET0001'
exec sp_DailyOrderStats '2025-10-23'
select * from fn_ServiceStats('2025-10-10', '2025-10-23')

--select * from Room where isAvailable = 1
--select * from OrderDetailRoom

-- Lấy ra toàn bộ phòng đã được đặt (isAvailable = 0) và status = N'Đặt' --> Lọc ra toàn bộ phòng có status = N'Đặt' --> Thực hiện tính năng check-in
--select r.roomID, r.description, r.isAvailable, r.roomTypeID, r.imgRoomSource, ordr.status
--from Room r
--JOIN OrderDetailRoom ordr
--ON r.roomID = ordr.roomID and ordr.status = N'Đặt'

-- Lấy ra toàn bộ phòng đã được đặt (isAvailable = 0) và status = N'Check-in' --> Lọc ra toàn bộ phòng có status = N'Check-in' --> Thực hiện tính năng check-out
--select r.roomID, r.description, r.isAvailable, r.roomTypeID, r.imgRoomSource, ordr.status
--from Room r
--JOIN OrderDetailRoom ordr
--ON r.roomID = ordr.roomID and ordr.status = N'Check-in'




--select * 
--from Room r
--JOIN RoomType rt 
--ON r.roomTypeID = rt.roomTypeID 
--where rt.typeName = N'Phòng đơn'

-- Room 1
--exec sp_CheckIn 'Room01'
--exec sp_CheckOut 'Room01'
--exec sp_PayOrder 'Ord00039'
--exec sp_GiaHanPhong 'Room01', '2025-09-02 13:00:00'

-- Phan Tấn Lộc đặt 3 phòng
--EXEC sp_BookRoom N'Phan Tấn Lộc',   '0911111103', 'loc.phan@mail.com',  '111111111113', 'Room01', 'Emp003',
--'2025-09-10 00:00:00', '2025-09-10 10:00:00', '2025-09-11 10:00:00', N'Ngày';

--EXEC sp_BookRoom N'Phan Tấn Lộc',   '0911111103', 'loc.phan@mail.com',  '111111111113', 'Room02', 'Emp003',
--'2025-09-10 00:00:00', '2025-09-10 10:00:00', '2025-09-11 10:00:00', N'Ngày';

--EXEC sp_BookRoom N'Phan Tấn Lộc',   '0911111103', 'loc.phan@mail.com',  '111111111113', 'Room03', 'Emp003',
--'2025-09-10 00:00:00', '2025-09-10 10:00:00', '2025-09-11 10:00:00', N'Ngày';

--exec sp_checkIn 'Room01'
--exec sp_CheckIn 'Room02'
--exec sp_checkIn 'Room03'

--EXEC sp_AddServiceToRoom 'Room01', N'Bia Tiger', 5
--EXEC sp_AddServiceToRoom 'Room02', N'Bia Tiger', 5
--EXEC sp_AddServiceToRoom 'Room03', N'Bia Tiger', 5


--exec sp_checkOut 'Room01'
--exec sp_checkOut 'Room02'
--exec sp_checkOut 'Room03'

--exec sp_PayOrder 'Ord00055'

--exec sp_GiaHanPhong 'Room02', '2025-10-24 10:00:00'

--exec sp_CancelBooking 'Room01'
--exec sp_CancelBooking 'Room02'

-- Dữ liệu ảo 
-- KH1: 9/05 – Phòng đơn 4 giờ
--EXEC sp_BookRoom N'Ngô Minh Hào', '0911111101', 'hao.ngo@mail.com', '111111111111',
--                  'Room01', 'Emp001',
--                  '2025-09-05 00:00:00', '2025-09-05 13:00:00', '2025-09-05 17:00:00', N'Giờ';
--EXEC sp_CheckIn  'Room01';
--EXEC sp_CheckOut 'Room01';
--DECLARE @o1 CHAR(8) = (SELECT TOP 1 o.orderID FROM [Order] o JOIN Customer c ON o.customerID=c.customerID WHERE c.phone='0911111101' ORDER BY o.orderID DESC);
--EXEC sp_PayOrder @o1;

---- KH2: 9/07 – Phòng đôi 1 đêm
--EXEC sp_BookRoom N'Đặng Thu Hà', '0911111102', 'ha.dang@mail.com', '111111111112',
--                  'Room05', 'Emp002',
--                  '2025-09-07 00:00:00', '2025-09-07 19:00:00', '2025-09-08 07:00:00', N'Đêm';
--EXEC sp_CheckIn  'Room05';
--EXEC sp_CheckOut 'Room05';
--DECLARE @o2 CHAR(8) = (SELECT TOP 1 o.orderID FROM [Order] o JOIN Customer c ON o.customerID=c.customerID WHERE c.phone='0911111102' ORDER BY o.orderID DESC);
--EXEC sp_PayOrder @o2;

---- KH3: 9/10 – Phòng đơn 1 ngày + dịch vụ
--EXEC sp_BookRoom N'Phan Tấn Lộc', '0911111103', 'loc.phan@mail.com', '111111111113',
--                  'Room02', 'Emp003',
--                  '2025-09-10 00:00:00', '2025-09-10 10:00:00', '2025-09-11 10:00:00', N'Ngày';
--EXEC sp_CheckIn  'Room02';
--EXEC sp_AddServiceToRoom 'Room02', N'Bia Tiger', 5;
--EXEC sp_CheckOut 'Room02';
--DECLARE @o3 CHAR(8) = (SELECT TOP 1 o.orderID FROM [Order] o JOIN Customer c ON o.customerID=c.customerID WHERE c.phone='0911111103' ORDER BY o.orderID DESC);
--EXEC sp_PayOrder @o3;

---- KH4: 9/12 – Phòng đôi 6 giờ
--EXEC sp_BookRoom N'Nguyễn Ái Vy', '0911111104', 'vy.nguyen@mail.com', '111111111114',
--                  'Room06', 'Emp001',
--                  '2025-09-12 00:00:00', '2025-09-12 14:00:00', '2025-09-12 20:00:00', N'Giờ';
--EXEC sp_CheckIn  'Room06';
--EXEC sp_CheckOut 'Room06';
--DECLARE @o4 CHAR(8) = (SELECT TOP 1 o.orderID FROM [Order] o JOIN Customer c ON o.customerID=c.customerID WHERE c.phone='0911111104' ORDER BY o.orderID DESC);
--EXEC sp_PayOrder @o4;

---- KH5: 9/15 – Phòng đơn 1 đêm + giặt ủi
--EXEC sp_BookRoom N'Vũ Thành Nam', '0911111105', 'nam.vu@mail.com', '111111111115',
--                  'Room03', 'Emp002',
--                  '2025-09-15 00:00:00', '2025-09-15 18:30:00', '2025-09-16 07:30:00', N'Đêm';
--EXEC sp_CheckIn  'Room03';
--EXEC sp_AddServiceToRoom 'Room03', N'Giặt ủi', 2;
--EXEC sp_CheckOut 'Room03';
--DECLARE @o5 CHAR(8) = (SELECT TOP 1 o.orderID FROM [Order] o JOIN Customer c ON o.customerID=c.customerID WHERE c.phone='0911111105' ORDER BY o.orderID DESC);
--EXEC sp_PayOrder @o5;

---- KH6: 9/24 – Phòng đôi 1 đêm + nước đóng chai
--EXEC sp_BookRoom N'Phạm Thảo Chi', '0911111108', 'chi.pham@mail.com', '111111111118',
--                  'Room05', 'Emp002',
--                  '2025-09-24 00:00:00', '2025-09-24 20:00:00', '2025-09-25 08:00:00', N'Đêm';
--EXEC sp_CheckIn  'Room05';
--EXEC sp_AddServiceToRoom 'Room05', N'Nước uống đóng chai', 4;
--EXEC sp_CheckOut 'Room05';
--DECLARE @o6 CHAR(8) = (SELECT TOP 1 o.orderID FROM [Order] o JOIN Customer c ON o.customerID=c.customerID WHERE c.phone='0911111108' ORDER BY o.orderID DESC);
--EXEC sp_PayOrder @o6;



--IF NOT EXISTS (SELECT 1 FROM Customer WHERE phone='0912000001')
--    INSERT INTO Customer(fullName, phone, email) VALUES (N'KH Tháng Trước 1','0912000001','pr1@mail.com');
--IF NOT EXISTS (SELECT 1 FROM Customer WHERE phone='0912000002')
--    INSERT INTO Customer(fullName, phone, email) VALUES (N'KH Tháng Trước 2','0912000002','pr2@mail.com');
--IF NOT EXISTS (SELECT 1 FROM Customer WHERE phone='0912000003')
--    INSERT INTO Customer(fullName, phone, email) VALUES (N'KH Tháng Trước 3','0912000003','pr3@mail.com');

--DECLARE @Cus1 CHAR(10) = (SELECT TOP 1 customerID FROM Customer WHERE phone='0912000001' ORDER BY customerID);
--DECLARE @Cus2 CHAR(10) = (SELECT TOP 1 customerID FROM Customer WHERE phone='0912000002' ORDER BY customerID);
--DECLARE @Cus3 CHAR(10) = (SELECT TOP 1 customerID FROM Customer WHERE phone='0912000003' ORDER BY customerID);

-- 2) Chèn order đã thanh toán trong tháng 09/2025
--INSERT INTO [Order](orderDate,total,employeeID,customerID,orderStatus) VALUES
--('2025-09-03T10:20:00',  450000, 'Emp001', @Cus1, N'Thanh toán'),
--('2025-09-05T19:10:00',  820000, 'Emp002', @Cus2, N'Thanh toán'),
--('2025-09-07T08:45:00',  290000, 'Emp003', @Cus1, N'Thanh toán'),
--('2025-09-10T12:00:00', 1200000, 'Emp002', @Cus3, N'Thanh toán'),
--('2025-09-12T21:30:00',  360000, 'Emp001', @Cus2, N'Thanh toán'),
--('2025-09-15T09:00:00',  980000, 'Emp001', @Cus1, N'Thanh toán'),
--('2025-09-18T18:15:00',  650000, 'Emp002', @Cus3, N'Thanh toán'),
--('2025-09-22T14:05:00',  510000, 'Emp003', @Cus2, N'Thanh toán'),
--('2025-09-25T20:40:00', 1350000, 'Emp002', @Cus1, N'Thanh toán'),
--('2025-09-28T11:25:00',  420000, 'Emp001', @Cus3, N'Thanh toán');
--GO




/* ===================== THÁNG 9/2025 – thêm dữ liệu ===================== */
---- 9/5: 4 giờ phòng đơn
--EXEC sp_BookRoom N'Ngô Minh Hào',   '0911111101', 'hao.ngo@mail.com',   '111111111111', 'Room01', 'Emp001',
--                  '2025-09-05 00:00:00', '2025-09-05 13:00:00', '2025-09-05 17:00:00', N'Giờ';
--EXEC sp_AddServiceToRoom 'Room01', N'Nước ngọt', 3;

---- 9/7: 1 đêm phòng đôi
--EXEC sp_BookRoom N'Đặng Thu Hà',    '0911111102', 'ha.dang@mail.com',   '111111111112', 'Room05', 'Emp002',
--                  '2025-09-07 00:00:00', '2025-09-07 19:00:00', '2025-09-08 07:00:00', N'Đêm';

---- 9/10: 1 ngày phòng đơn
--EXEC sp_BookRoom N'Phan Tấn Lộc',   '0911111103', 'loc.phan@mail.com',  '111111111113', 'Room02', 'Emp003',
----                  '2025-09-10 00:00:00', '2025-09-10 10:00:00', '2025-09-11 10:00:00', N'Ngày';
--EXEC sp_AddServiceToRoom 'Room02', N'Bia Tiger', 5;

---- 9/12: 6 giờ phòng đôi
--EXEC sp_BookRoom N'Nguyễn Ái Vy',   '0911111104', 'vy.nguyen@mail.com', '111111111114', 'Room06', 'Emp001',
--                  '2025-09-12 00:00:00', '2025-09-12 14:00:00', '2025-09-12 20:00:00', N'Giờ';

---- 9/15: 1 đêm phòng đơn
--EXEC sp_BookRoom N'Vũ Thành Nam',   '0911111105', 'nam.vu@mail.com',    '111111111115', 'Room03', 'Emp002',
--                  '2025-09-15 00:00:00', '2025-09-15 18:30:00', '2025-09-16 07:30:00', N'Đêm';
--EXEC sp_AddServiceToRoom 'Room03', N'Giặt ủi', 2;

---- 9/18: 1 ngày phòng đôi
--EXEC sp_BookRoom N'Lâm Quỳnh Anh',  '0911111106', 'anh.lam@mail.com',   '111111111116', 'Room07', 'Emp003',
--                  '2025-09-18 00:00:00', '2025-09-18 09:30:00', '2025-09-19 09:30:00', N'Ngày';

---- 9/22: 3 giờ phòng đơn
--EXEC sp_BookRoom N'Trần Minh Đức',  '0911111107', 'duc.tran@mail.com',  '111111111117', 'Room01', 'Emp001',
--                  '2025-09-22 00:00:00', '2025-09-22 15:00:00', '2025-09-22 18:00:00', N'Giờ';

---- 9/24: 1 đêm phòng đôi
--EXEC sp_BookRoom N'Phạm Thảo Chi',  '0911111108', 'chi.pham@mail.com',  '111111111118', 'Room05', 'Emp002',
--                  '2025-09-24 00:00:00', '2025-09-24 20:00:00', '2025-09-25 08:00:00', N'Đêm';
--EXEC sp_AddServiceToRoom 'Room05', N'Nước uống đóng chai', 4;

---- 9/27: 1 ngày phòng đơn
--EXEC sp_BookRoom N'Đỗ Tấn Khoa',    '0911111109', 'khoa.do@mail.com',   '111111111119', 'Room02', 'Emp003',
--                  '2025-09-27 00:00:00', '2025-09-27 11:00:00', '2025-09-28 11:00:00', N'Ngày';

---- 9/29: 5 giờ phòng đôi
--EXEC sp_BookRoom N'Hoàng Gia Bảo',  '0911111110', 'bao.hoang@mail.com', '111111111120', 'Room06', 'Emp001',
--                  '2025-09-29 00:00:00', '2025-09-29 13:00:00', '2025-09-29 18:00:00', N'Giờ';
--GO

--/* ===================== THÁNG 10/2025 – thêm vài bản ghi ===================== */
---- 10/03: 1 ngày phòng đôi
--EXEC sp_BookRoom N'Nguyễn Thanh Tú', '0911111121', 'tu.nguyen@mail.com', '222222222221', 'Room07', 'Emp002',
--                  '2025-10-03 00:00:00', '2025-10-03 10:00:00', '2025-10-04 10:00:00', N'Ngày';

---- 10/06: 4 giờ phòng đơn
--EXEC sp_BookRoom N'Lê Mỹ Duyên',     '0911111122', 'duyen.le@mail.com',  '222222222222', 'Room03', 'Emp003',
--                  '2025-10-06 00:00:00', '2025-10-06 14:00:00', '2025-10-06 18:00:00', N'Giờ';

---- 10/09: 1 đêm phòng đôi
--EXEC sp_BookRoom N'Đặng Hữu Phúc',   '0911111123', 'phuc.dang@mail.com', '222222222223', 'Room05', 'Emp001',
--                  '2025-10-09 00:00:00', '2025-10-09 19:30:00', '2025-10-10 08:00:00', N'Đêm';

---- 10/12: 1 ngày phòng đơn + dịch vụ
--EXEC sp_BookRoom N'Trương Bảo Vy',   '0911111124', 'vy.truong@mail.com', '222222222224', 'Room02', 'Emp002',
--                  '2025-10-12 00:00:00', '2025-10-12 09:00:00', '2025-10-13 09:00:00', N'Ngày';
--EXEC sp_AddServiceToRoom 'Room02', N'Bia Tiger', 6;
--GO

--delete from [dbo].[Order]
--delete from OrderDetailRoom
--delete from OrderDetailService

--SELECT odr.orderID, c.fullName, odr.roomID, odr.checkInDate, odr.checkOutDate, odr.status 
--FROM OrderDetailRoom odr 
--JOIN [Order] o ON o.orderID = odr.orderID 
--JOIN Customer c ON c.customerID = o.customerID  
--WHERE CAST(odr.checkInDate AS DATE) >= CAST(GETDATE() AS DATE) 
--AND CAST(odr.checkInDate AS DATE) < CAST(DATEADD(DAY, 7, GETDATE()) AS DATE) 
--ORDER BY odr.checkInDate ASC