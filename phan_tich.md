# Phân tích logic lỗi trong phương thức `updateStock`

Dựa trên mã nguồn và các quy tắc nghiệp vụ, hiện tại phương thức `updateStock` đang tồn tại **2 lỗi logic chính** khiến
số lượng tồn kho không chính xác.

---

# 1. Lỗi kiểm tra tồn kho âm

## Quy tắc nghiệp vụ

> Không cho phép số lượng tồn kho xuống dưới 0.

---

## Đoạn code hiện tại

```java
if(newStock< 0){
        throw new

IllegalStateException("Resulting stock would be negative");
}
```

---

## Vấn đề

Khi người dùng cố gắng trừ số lượng lớn hơn tồn kho hiện có:

Ví dụ:

- Tồn kho hiện tại: `5`
- quantityChange: `-10`

Kết quả:

```java
newStock =5+(-10)=-5
```

Hệ thống phát hiện lỗi nhưng lại ném:

```java
IllegalStateException
```

---

## Sai logic ở đâu?

Đây là lỗi do dữ liệu đầu vào hoặc hành động nghiệp vụ không hợp lệ, nên phù hợp hơn với:

```java
IllegalArgumentException
```

Không phải lỗi trạng thái hệ thống (`IllegalStateException`).

---

## Hậu quả

- Exception không đúng với bản chất nghiệp vụ
- Unit test có thể fail nếu kiểm tra đúng loại exception
- Khó xử lý thống nhất ở tầng controller/API

---

# 2. Lỗi không lưu dữ liệu vào database

## Đoạn code hiện tại

```java
product.setStockQuantity(newStock);

// productRepository.save(product);
```

---

## Vấn đề

Sau khi cập nhật tồn kho trong object `product`, hệ thống lại quên gọi:

```java
productRepository.save(product);
```

---

## Sai logic ở đâu?

Object chỉ được thay đổi trong bộ nhớ RAM nhưng không được persist xuống database.

Điều này vi phạm quy tắc nghiệp vụ:

> Mọi thay đổi tồn kho phải được ghi nhận vào cơ sở dữ liệu.

---

## Hậu quả

Ví dụ:

- Tồn kho ban đầu: `10`
- Gọi updateStock(..., -2)

Trong RAM:

```java
stock =8
```

Nhưng database:

```java
stock =10
```

=> Dẫn tới:

- tồn kho sai lệch
- overselling
- dữ liệu không đồng bộ

---

# Tổng kết lỗi logic

| STT | Lỗi                | Nguyên nhân                                                     | Hậu quả                          |
|-----|--------------------|-----------------------------------------------------------------|----------------------------------|
| 1   | Sai loại exception | Dùng `IllegalStateException` thay vì `IllegalArgumentException` | Sai nghiệp vụ, test fail         |
| 2   | Không gọi `save()` | Quên persist dữ liệu xuống database                             | Tồn kho không được cập nhật thật |

---

# Kết luận

Phương thức `updateStock()` hiện có **2 lỗi logic chính**:

1. Ném sai loại exception khi tồn kho bị âm.
2. Không gọi `productRepository.save(product)` sau khi cập nhật tồn kho.

Hai lỗi này khiến:

- dữ liệu tồn kho không chính xác,
- có thể gây overselling,
- và làm hệ thống không đảm bảo tính nhất quán dữ liệu.