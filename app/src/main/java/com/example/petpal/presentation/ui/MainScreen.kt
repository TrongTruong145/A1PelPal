package com.example.petpal.presentation.ui

import android.R.attr.onClick
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.example.petpal.R


@Composable
fun MainScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFAEE)) // màu nền kem
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_petpal), // thêm logo vào drawable
            contentDescription = "PetPal Logo",
            modifier = Modifier.size(300.dp) )

        Spacer(modifier = Modifier.height(16.dp))


        Button(
            onClick = { navController.navigate("home") },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp), // chiều cao
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFEDA600),
                contentColor = Color.White
            )
        ) {
            Text(
                text = "🏠 Home",
                fontSize = 20.sp,
                style = MaterialTheme.typography.bodyLarge)
        }


        Spacer(modifier = Modifier.height(16.dp))


        Button(
            onClick = { navController.navigate("report_lost") },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp), // chiều cao
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFAD320C),
                contentColor = Color.White
            )
        ) {
            Text(
                text = "🏠 I Lost My Pet",
                fontSize = 20.sp,
                style = MaterialTheme.typography.bodyLarge)
        }


        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("report_found") },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp), // chiều cao
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4DAD0C),
                contentColor = Color.White
            )
        ) {
            Text(
                text = "🐾 I Found Someone Pet",
                fontSize = 20.sp,
                style = MaterialTheme.typography.bodyLarge)
        }

    }
}
