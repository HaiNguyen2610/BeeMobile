package com.example.du_an1_qldt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.du_an1_qldt.Adapter.CartAdapter;
import com.example.du_an1_qldt.DAO.CartDao;
import com.example.du_an1_qldt.DAO.OrderDAO;
import com.example.du_an1_qldt.model.Cart;
import com.example.du_an1_qldt.model.Order;
import com.example.du_an1_qldt.model.OrderDetail;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Frag_GioHang extends Fragment implements TotalPriceListener {
    RecyclerView recyclerView;
    ArrayList<Cart> cartArrayList;
    CartDao cartDao;
    CartAdapter cartAdapter;
    Adapter adapter;
    TextView tv_price;
    LinearLayoutManager linearLayoutManager;
    Button btnOrder;
    OrderDAO orderDAO;
    Date date;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_frag_gio_hang, container, false);
        return v;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.rcv_cart);
        tv_price=view.findViewById(R.id.tv_price);
        btnOrder=view.findViewById(R.id.btn_order);
        cartDao = new CartDao(getContext());
        cartArrayList = cartDao.getlistCart();
        cartAdapter = new CartAdapter(getContext(), cartArrayList);
        recyclerView.setAdapter(cartAdapter);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        cartAdapter.setOnTotalPriceChangedListener(this);
        cartAdapter.notifyDataSetChanged();
        calculateTotalPrice();
btnOrder.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("thongtin", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("manguoidung", "");
        date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String formattedDate = dateFormat.format(date);
//        orderDAO = new OrderDAO(getContext());
//        Order order=new Order();
//        order.setIdUser(Integer.parseInt(id));
//        order.setStatusOrder("Chờ xác nhận");
//        order.setDateOrder(formattedDate);
//        int check = orderDAO.createOrder(order);
//        if (check > 0) {
//            Toast.makeText(getContext(), "ĐÃ TẠO ĐƠN HÀNG", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(getContext(), "TẠO ĐƠN HÀNG THẤT BẠI", Toast.LENGTH_SHORT).show();
//        }
        cartDao= new CartDao(getContext());
        ArrayList<Cart> carts= cartDao.getlistCart();
        orderDAO= new OrderDAO(getContext());
        Order order= new Order();
        order.setIdUser(Integer.parseInt(id));
        order.setStatusOrder("Chờ xác nhận");
        order.setDateOrder(formattedDate);
        long orderId = orderDAO.createOrder(order);
        for (Cart cart: carts) {
            OrderDetail orderDetail= new OrderDetail();
            orderDetail.setId((int) orderId);
            orderDetail.setPrice(cart.getPrice());
            orderDetail.setIdProduct(cart.getIdPhone());
            orderDetail.setQuantity(cart.getQuantity());
            orderDAO.createOrderDetail(orderDetail);
            cartDao.deleteRowCart(cart);
        }
        Toast.makeText(getContext(), "Đặt hàng thành công", Toast.LENGTH_SHORT).show();

    }
});
    }
    @Override
    public void onTotalPriceChanged(double totalPrice) {
        tv_price.setText(String.valueOf(totalPrice));
    }
    private void calculateTotalPrice() {
        double totalPrice = 0;
        for (Cart cartItem : cartArrayList) {
            totalPrice += cartItem.getPrice(); // Giả sử getPrice() là phương thức lấy giá của một sản phẩm trong giỏ hàng
        }
        tv_price.setText(String.valueOf(totalPrice));
    }
}