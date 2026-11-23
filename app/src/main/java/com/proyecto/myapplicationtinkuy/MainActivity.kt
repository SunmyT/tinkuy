package com.proyecto.myapplicationtinkuy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.proyecto.myapplicationtinkuy.Product.Alpaca
import com.proyecto.myapplicationtinkuy.Product.Ceramic
import com.proyecto.myapplicationtinkuy.Product.Retablo
import com.proyecto.myapplicationtinkuy.Product.Sheep
import com.proyecto.myapplicationtinkuy.ui.theme.MyApplicationTinkuyTheme
import com.proyecto.myapplicationtinkuy.ui.screens.login.LoginScreen
import com.proyecto.myapplicationtinkuy.ui.screens.welcome.WelcomeScreen
import com.proyecto.myapplicationtinkuy.ui.screens.login.RegisterScreen
import com.proyecto.myapplicationtinkuy.ui.screens.login.PasswordScreen
import com.proyecto.myapplicationtinkuy.ui.screens.login.VerifyScreen
import com.proyecto.myapplicationtinkuy.ui.screens.login.ForgotPasswordScreen
import com.proyecto.myapplicationtinkuy.ui.screens.HomeScreen
import com.proyecto.myapplicationtinkuy.ui.components.Grid
import com.proyecto.myapplicationtinkuy.ui.components.Thumb
import com.proyecto.myapplicationtinkuy.ui.screens.NotificationsScreen
import com.proyecto.myapplicationtinkuy.ui.components.Page
import com.proyecto.myapplicationtinkuy.ui.components.Header
import com.proyecto.myapplicationtinkuy.ui.order.OrderFilter
import com.proyecto.myapplicationtinkuy.ui.order.OrderStatus
import com.proyecto.myapplicationtinkuy.ui.order.ShopOrder
import com.proyecto.myapplicationtinkuy.ui.order.ShopOrderItem
import com.proyecto.myapplicationtinkuy.ui.order.OrdersScreen
import com.proyecto.myapplicationtinkuy.ui.order.TrackingScreen
import com.proyecto.myapplicationtinkuy.ui.screens.CatalogScreen
import com.proyecto.myapplicationtinkuy.ui.order.sampleOrders
import com.proyecto.myapplicationtinkuy.ui.theme.AppBackground
import com.proyecto.myapplicationtinkuy.ui.theme.Ink
import com.proyecto.myapplicationtinkuy.ui.theme.Line
import com.proyecto.myapplicationtinkuy.ui.theme.Muted


internal val Accent = Color(0xFF17594D)
private val AccentSoft = Color(0xFFE4F0EA)
private val Green = Color(0xFF4CAF50)
private val Amber = Color(0xFFB7832C)
private val Blue = Color(0xFF1781F7)

internal enum class Screen {
    Welcome, Login, Register, Password, Verify, Forgot,
    Home, Catalog, Detail, Producer, Cart, OrderDone, Quote, QuoteDone,
    Orders, Tracking, Profile, Account, History, Notifications, Settings,
    Help, Sync, Favorites, About, Requests, Proposals
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val lightBars = SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.WHITE)
        enableEdgeToEdge(statusBarStyle = lightBars, navigationBarStyle = lightBars)
        setContent {
            MyApplicationTinkuyTheme(darkTheme = false) {
                Surface(Modifier.fillMaxSize(), color = AppBackground) {
                    Box(Modifier.fillMaxSize().safeDrawingPadding()) {
                        TinkuyApp()
                    }
                }
            }
        }
    }
}

@Composable
private fun TinkuyApp() {
    var history by rememberSaveable { mutableStateOf(arrayListOf(Screen.Welcome.name)) }
    var homeCategory by rememberSaveable { mutableStateOf(Category.All) }
    var catalogCategory by rememberSaveable { mutableStateOf(Category.All) }
    var homeQuery by rememberSaveable { mutableStateOf("") }
    var catalogQuery by rememberSaveable { mutableStateOf("") }
    var product by rememberSaveable { mutableStateOf(Alpaca) }
    var createdOrders by rememberSaveable { mutableStateOf(arrayListOf<ShopOrder>()) }
    var orderFilter by rememberSaveable { mutableStateOf(OrderFilter.All) }
    var selectedOrderId by rememberSaveable { mutableIntStateOf(sampleOrders.first().id) }
    var handledProposals by rememberSaveable { mutableStateOf(arrayListOf<String>()) }
    var quantities by rememberSaveable {
        mutableStateOf(IntArray(Product.entries.size).apply {
            this[Alpaca.ordinal] = 1
            this[Ceramic.ordinal] = 1
        })
    }
    val screen = Screen.valueOf(history.last())
    val go: (Screen) -> Unit = { destination ->
        if (destination != screen) {
            val isSessionBoundary = destination == Screen.Welcome ||
                (destination == Screen.Home && history.first() == Screen.Welcome.name)
            val previousIndex = history.indexOf(destination.name)
            history = when {
                isSessionBoundary -> arrayListOf(destination.name)
                previousIndex >= 0 -> ArrayList(history.take(previousIndex + 1))
                else -> ArrayList(history + destination.name)
            }
        }
    }
    val back: () -> Unit = {
        if (history.size > 1) history = ArrayList(history.dropLast(1))
    }
    val openProduct: (Product) -> Unit = { selected ->
        product = selected
        go(Screen.Detail)
    }
    val changeQuantity: (Product, Int) -> Unit = { item, delta ->
        quantities = quantities.copyOf().apply {
            this[item.ordinal] = (this[item.ordinal] + delta).coerceAtLeast(0)
        }
    }
    val orders = createdOrders + sampleOrders
    val openOrder: (ShopOrder) -> Unit = { order ->
        selectedOrderId = order.id
        go(Screen.Tracking)
    }
    val createOrder: (List<ShopOrderItem>) -> ShopOrder = { items ->
        val nextId = (createdOrders.firstOrNull()?.id ?: sampleOrders.first().id) + 1
        val order = ShopOrder(nextId, java.time.LocalDate.now(), OrderStatus.Pending, items)
        createdOrders = ArrayList(listOf(order) + createdOrders)
        orderFilter = OrderFilter.All
        order
    }
    val confirmCart: () -> Unit = {
        val items = Product.entries.mapNotNull { item ->
            quantities[item.ordinal].takeIf { it > 0 }?.let { ShopOrderItem(item, it) }
        }
        if (items.isNotEmpty()) {
            createOrder(items)
            quantities = IntArray(Product.entries.size)
            go(Screen.OrderDone)
        }
    }
    BackHandler(enabled = history.size > 1, onBack = back)
    when (screen) {
        Screen.Welcome -> WelcomeScreen(
            onLoginClick = { go(Screen.Login) },
            onCreateAccountClick = { go(Screen.Register) }
        )
        Screen.Login -> LoginScreen(
            onLoginSuccess = { go(Screen.Home) },
            onForgotPassword = { go(Screen.Forgot) },
            onRegister = { go(Screen.Register) }
        )
        Screen.Register -> RegisterScreen(
            onBack = { go(Screen.Login) },
            onNext = { go(Screen.Password) }
        )
        Screen.Password -> PasswordScreen(
            onBack = { go(Screen.Register) },
            onCreateAccount = { go(Screen.Verify) }
        )
        Screen.Verify -> VerifyScreen(
            onBack = { go(Screen.Password) },
            onVerify = { go(Screen.Home) },
            onResend = { /* Pendiente de integrar SMS */ }
        )
        Screen.Forgot -> ForgotPasswordScreen(
            onBack = { go(Screen.Login) },
            onSendInstructions = { go(Screen.Login) }
        )
        Screen.Home -> HomeScreen(
            go,
            homeCategory,
            { homeCategory = it },
            homeQuery,
            { homeQuery = it },
            openProduct
        )
        Screen.Catalog -> CatalogScreen(
            go,
            catalogCategory,
            { catalogCategory = it },
            catalogQuery,
            { catalogQuery = it },
            openProduct
        )
        Screen.Detail -> Detail(product, go, back) { changeQuantity(product, 1) }
        Screen.Producer -> Producer(product, go, openProduct)
        Screen.Cart -> if (quantities.any { it > 0 }) Cart(go, quantities, changeQuantity, confirmCart) else EmptyCart(go)
        Screen.OrderDone -> OrderDone(go)
        Screen.Quote -> Quote(product, go)
        Screen.QuoteDone -> QuoteDone(product, go)
        Screen.Orders -> OrdersScreen(
            orders,
            orderFilter,
            { orderFilter = it },
            openOrder,
            go,
            back
        )

        Screen.Tracking -> TrackingScreen(
            orders.firstOrNull { it.id == selectedOrderId } ?: orders.first(),
            go,
            back
        )
        Screen.Profile -> Profile(go)
        Screen.Account -> Account(go)
        Screen.History -> History(go)
        Screen.Notifications -> NotificationsScreen(go, back)
        Screen.Settings -> Settings(go)
        Screen.Help -> Help(go)
        Screen.Sync -> Sync(go)
        Screen.Favorites -> Favorites(go, openProduct)
        Screen.About -> About(go)
        Screen.Requests -> Requests(go)
        Screen.Proposals -> Proposals(
            go,
            handledProposals,
            onAccept = { item ->
                handledProposals = ArrayList(handledProposals + item.product.name)
                openOrder(createOrder(listOf(item)))
            },
            onReject = { item ->
                handledProposals = ArrayList(handledProposals + item.product.name)
            }
        )
    }
}

@Composable
private fun Detail(product: Product, go: (Screen) -> Unit, back: () -> Unit, add: () -> Unit) = Page(Screen.Detail, go) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Box(Modifier.fillMaxWidth().height(352.dp)) {
            Thumb(product, Modifier.matchParentSize())
            IconButton(onClick = back, modifier = Modifier.padding(12.dp).background(Color.White, CircleShape)
                .semantics { contentDescription = "Volver" }) {
                TinkuyIcon(AppIcon.Back, tint = Ink)
            }
            IconButton(onClick = { go(Screen.Favorites) }, modifier = Modifier.align(Alignment.TopEnd)
                .padding(12.dp).background(Color.White, CircleShape).semantics { contentDescription = "Favoritos" }) {
                TinkuyIcon(AppIcon.Heart, tint = Accent)
            }
            Text(
                "Foto referencial", Modifier.align(Alignment.BottomEnd).padding(12.dp)
                    .background(Color.White, RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 3.dp),
                fontSize = 12.sp
            )
        }
        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(topStart = 19.dp, topEnd = 19.dp)).background(Color.White).padding(26.dp, 23.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) { Text(product.title, Modifier.weight(1f), fontSize = 23.sp); Text("★ ★ ★ ★ (5.0)", fontSize = 12.sp) }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(product.price, Modifier.alignByBaseline(), fontSize = 25.sp)
                Text("por unidad", Modifier.alignByBaseline(), color = Muted, fontSize = 12.sp)
            }
            Spacer(Modifier.height(15.dp))
            Text("Disponible: 20 unidades", fontSize = 12.sp); Text(product.description, fontSize = 12.sp, color = Color(0xFF656565)); Spacer(Modifier.height(14.dp))
            Row(Modifier.fillMaxWidth().height(52.dp).background(Color(0xFFFAFAFA)).clickable { go(Screen.Producer) }, verticalAlignment = Alignment.CenterVertically) {
                Placeholder(Modifier.size(45.dp).padding(4.dp)); Column(Modifier.padding(start = 9.dp).weight(1f)) { Text("Producido por", fontSize = 9.sp, color = Muted); Text(product.producer, fontSize = 14.sp); Text(product.region, fontSize = 9.sp) }; Text("✓", Modifier.size(20.dp).clip(CircleShape).background(Color(0xFF536BFF)).padding(start = 5.dp, top = 1.dp), color = Color.White)
            }
            Spacer(Modifier.height(11.dp)); Text("Opiniones de compradores", fontSize = 16.sp); Divider(Modifier.width(76.dp).padding(top = 10.dp), color = Ink, thickness = 4.dp)
            Row(Modifier.padding(top = 9.dp), verticalAlignment = Alignment.CenterVertically) { Avatar(Modifier.size(31.dp), false); Text("Jane Doe", Modifier.padding(start = 10.dp)); Spacer(Modifier.weight(1f)); Text("★ ★ ★ ★", fontSize = 14.sp) }
            Spacer(Modifier.height(16.dp)); Row(horizontalArrangement = Arrangement.spacedBy(13.dp)) { BlackButton("Cotizar", Modifier.weight(1f)) { go(Screen.Quote) }; BlackButton("Agregar", Modifier.weight(1f)) { add(); go(Screen.Cart) } }
        }
    }
}

@Composable
private fun Producer(product: Product, go: (Screen) -> Unit, openProduct: (Product) -> Unit) = Page(Screen.Producer, go) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(top = 36.dp)) {
        IconButton(onClick = { go(Screen.Detail) }, modifier = Modifier.padding(horizontal = 18.dp)) {
            TinkuyIcon(AppIcon.Back, tint = Ink, contentDescription = "Volver")
        }
        Spacer(Modifier.height(3.dp))
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Placeholder(Modifier.size(100.dp)); Text(product.producer, fontSize = 23.sp); Text(product.region, fontSize = 11.sp, color = Muted)
            Row { Text("4.0", fontSize = 22.sp); Text(" | ", fontSize = 22.sp); Text("★★★★★", color = Color(0xFFFFC526), fontSize = 18.sp) }; Text("(122 ratings)", color = Muted, fontSize = 14.sp); Spacer(Modifier.height(17.dp))
            if (product.producer == Alpaca.producer) {
                Text("Asociación de 18 mujeres tejedoras que preservan técnicas ancestrales y trabajan con fibra natural de alpaca.", Modifier.padding(horizontal = 24.dp), textAlign = TextAlign.Center, color = Color(0xFF595959), fontSize = 13.sp)
            }
        }
        Spacer(Modifier.height(19.dp)); Tabs(listOf("Productos", "Opiniones", "Acerca de"), 0)
        Grid(Product.entries.filter { it.producer == product.producer }, openProduct, Modifier.padding(18.dp))
    }
}

@Composable
private fun Cart(
    go: (Screen) -> Unit,
    quantities: IntArray,
    changeQuantity: (Product, Int) -> Unit,
    onConfirm: () -> Unit
) = Page(Screen.Cart, go) {
    Column(Modifier.fillMaxSize()) {
        Header("Carrito") { go(Screen.Home) }
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            Product.entries.forEach { item ->
                val count = quantities[item.ordinal]
                if (count > 0) {
                    CartLine(item, count, { changeQuantity(item, -1) }, { changeQuantity(item, 1) })
                    HorizontalDivider(color = Line)
                }
            }
        }
        val total = Product.entries.sumOf { it.unitPrice * quantities[it.ordinal] }
        Row(Modifier.fillMaxWidth().padding(horizontal = 35.dp), horizontalArrangement = Arrangement.SpaceBetween) { Text("Total", fontSize = 23.sp); Text(soles(total), fontSize = 23.sp) }
        Spacer(Modifier.height(24.dp))
        BlackButton("Confirmar", Modifier.fillMaxWidth().padding(horizontal = 23.dp), onConfirm)
        Spacer(Modifier.height(18.dp))
    }
}

@Composable
private fun EmptyCart(go: (Screen) -> Unit) = Page(Screen.Cart, go) {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Header("Carrito") { go(Screen.Home) }
        Box(
            Modifier.padding(top = 24.dp, bottom = 16.dp).size(88.dp).background(AccentSoft, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            TinkuyIcon(AppIcon.Cart, Modifier.size(42.dp), Accent)
        }
        Text("Tu carrito está vacío", color = Ink, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
        Text("Selecciona los productos que deseas adquirir para", color = Color(0xFF676767), fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp)); Text("añadirlo al carrito", color = Color(0xFF676767), fontSize = 12.sp)
        Spacer(Modifier.height(27.dp)); BlackButton("Volver al Inicio", Modifier.width(214.dp)) { go(Screen.Home) }; Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun OrderDone(go: (Screen) -> Unit) = Page(Screen.OrderDone, go) { Success("Pedido Registrado", "Tu pedido está en estado Pendiente. Te avisaremos\ncuando el productor lo confirme.", "Ver mis pedidos") { go(Screen.Orders) } }

@Composable
private fun Quote(product: Product, go: (Screen) -> Unit) {
    var count by rememberSaveable { mutableStateOf("1") }; var note by rememberSaveable { mutableStateOf("") }
    Page(Screen.Quote, go) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 23.dp)) {
            Header("Solicitar cotización") { go(Screen.Detail) }; Divider(color = Line); Spacer(Modifier.height(35.dp))
            Row(verticalAlignment = Alignment.CenterVertically) { Thumb(product, Modifier.size(70.dp)); Column(Modifier.padding(start = 14.dp)) { Text(product.title, fontSize = 18.sp); Text(product.maker, fontSize = 12.sp, color = Muted) } }
            Spacer(Modifier.height(37.dp)); Field("Cantidad requerida *", count, { count = it }, key = KeyboardType.Number); Spacer(Modifier.height(19.dp)); Field("Observaciones o requerimientos", note, { note = it }, Modifier.fillMaxWidth().height(110.dp), single = false)
            Spacer(Modifier.height(27.dp)); Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color(0xFFFAFAFA)).padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Text("☁", fontSize = 28.sp); Column(Modifier.padding(start = 15.dp)) { Text("Necesitas conexión para enviar la solicitud.", fontSize = 10.sp, color = Color(0xFF5E5E5E)); Text("Si estás sin conexión, guardaremos el\nborrador.", fontSize = 10.sp, color = Color(0xFF5E5E5E)) } }
            Spacer(Modifier.height(32.dp)); BlackButton("Enviar Solicitud", Modifier.fillMaxWidth().padding(horizontal = 10.dp)) { go(Screen.QuoteDone) }
        }
    }
}

@Composable
private fun QuoteDone(product: Product, go: (Screen) -> Unit) = Page(Screen.QuoteDone, go, false) { Success("Solicitud enviada", "${product.producer} recibió tu solicitud. Podrás revisar su propuesta en Mis cotizaciones.", "Ver mis cotizaciones") { go(Screen.Requests) } }

@Composable
private fun Profile(go: (Screen) -> Unit) = Page(Screen.Profile, go) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(32.dp)); Avatar(Modifier.size(106.dp)); Spacer(Modifier.height(9.dp)); Text("Usuario A", fontSize = 18.sp, fontWeight = FontWeight.Bold); Text("usuarioTinkuy@gmail.com", color = Muted, fontSize = 14.sp); Spacer(Modifier.height(14.dp))
        Menu("♙", "Mi cuenta") { go(Screen.Account) }; Menu("▤", "Mis pedidos") { go(Screen.Orders) }; Menu("≡", "Historial Comercial") { go(Screen.History) }; Menu("♟", "Notificaciones") { go(Screen.Notifications) }; Menu("⚙", "Configuración") { go(Screen.Settings) }; Menu("♧", "Ayuda") { go(Screen.Help) }
        Spacer(Modifier.height(24.dp))
        Row(Modifier.fillMaxWidth().padding(horizontal = 33.dp).clip(RoundedCornerShape(7.dp)).background(Color(0xFFFAFAFA)).padding(12.dp), verticalAlignment = Alignment.CenterVertically) { Text("✿", fontSize = 30.sp); Column(Modifier.padding(start = 10.dp)) { Text("¿También produces artesanías?", fontSize = 11.sp); Text("Solicita la habilitación de tu perfil comercial\nComenzar solicitud ›", fontSize = 9.sp, color = Color(0xFF555555)) } }
        Text("⇥ Log Out", Modifier.fillMaxWidth().padding(35.dp, 20.dp).clickable { go(Screen.Welcome) }, color = Color.Red, fontSize = 16.sp)
    }
}

@Composable
private fun Account(go: (Screen) -> Unit) {
    var name by rememberSaveable { mutableStateOf("Usuario A") }; var mail by rememberSaveable { mutableStateOf("usuarioTinkuy@gmail.com") }; var phone by rememberSaveable { mutableStateOf("99384271") }
    Page(Screen.Account, go) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 25.dp)) {
            Header("Mi cuenta") { go(Screen.Profile) }
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { Avatar(Modifier.size(104.dp)); Text("◉", Modifier.align(Alignment.BottomCenter).padding(start = 87.dp).size(31.dp).clip(CircleShape).background(Color.Black).padding(8.dp), color = Color.White, fontSize = 12.sp) }
            Spacer(Modifier.height(25.dp)); Row(Modifier.fillMaxWidth()) { Text("Datos personales", fontWeight = FontWeight.SemiBold, fontSize = 17.sp); Spacer(Modifier.weight(1f)); Text("Editar", fontSize = 12.sp, textDecoration = TextDecoration.Underline) }; Spacer(Modifier.height(10.dp))
            Field("Nombres Completos", name, { name = it }); Spacer(Modifier.height(11.dp)); Field("Correo electrónico", mail, { mail = it }); Spacer(Modifier.height(11.dp)); Field("Teléfono", phone, { phone = it }); Spacer(Modifier.height(11.dp))
            Field("Departamento", "Cusco", {}, readOnly = true); Spacer(Modifier.height(11.dp)); Field("Provincia", "Cusco", {}, readOnly = true); Spacer(Modifier.height(11.dp)); Field("Distrito", "San Sebastian", {}, readOnly = true)
            Text("Los campos están bloqueados.", Modifier.padding(start = 9.dp, top = 15.dp), fontSize = 10.sp); BlackButton("Guardar cambios", Modifier.width(180.dp).align(Alignment.CenterHorizontally)) {}; Spacer(Modifier.height(18.dp))
        }
    }
}

@Composable
private fun History(go: (Screen) -> Unit) = Page(Screen.History, go) {
    Column(Modifier.fillMaxSize()) {
        Header("Historial comercial") { go(Screen.Profile) }
        Text("Aquí encontrarás las operaciones comerciales\nen las que participas.", Modifier.padding(26.dp, 23.dp), color = Color(0xFF616161), fontSize = 14.sp, lineHeight = 26.sp)
        Menu("♙", "Solicitudes de cotización", 30.dp) { go(Screen.Requests) }; Menu("▤", "Propuestas recibidas", 30.dp) { go(Screen.Proposals) }; Menu("⚙", "Pedidos", 30.dp) { go(Screen.Orders) }
    }
}

@Composable
private fun Settings(go: (Screen) -> Unit) = Page(Screen.Settings, go) {
    Column(Modifier.fillMaxSize()) {
        Header("Configuración") { go(Screen.Profile) }; Text("Gestiona tu información en Tinkuy", Modifier.padding(27.dp, 26.dp), color = Color(0xFF616161), fontSize = 14.sp)
        Menu("↔", "Sincronización", 30.dp) { go(Screen.Sync) }; Menu("ⓘ", "Acerca de Tinkuy", 30.dp) { go(Screen.About) }
        Text("Tinkuy permite consultar información almacenada\nsin conexión y sincronizar los cambios pendientes\ncuando vuelve la red.", Modifier.padding(35.dp, 34.dp).clip(RoundedCornerShape(5.dp)).background(Color(0xFFF0F0F0)).padding(12.dp), fontSize = 11.sp, lineHeight = 16.sp)
    }
}

@Composable
private fun Help(go: (Screen) -> Unit) = Page(Screen.Help, go) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Header("Ayuda") { go(Screen.Profile) }; Text("Soporte para tu cuenta en Tinkuy", Modifier.padding(27.dp, 27.dp, 27.dp, 35.dp), color = Color(0xFF616161), fontSize = 14.sp)
        Text("Antes de contactarnos", Modifier.padding(27.dp, 7.dp), fontSize = 15.sp, fontWeight = FontWeight.Bold)
        HelpLine("⌁", "Revisa tu conexión", "Algunas acciones necesitan internet"); HelpLine("↔", "Sincroniza tus cambios", "Envía los cambios pendientes cuando\nvuelva la red"); HelpLine("⟳", "Reinicia la aplicación", "Ciérrala y vuelve a abrirla")
        Spacer(Modifier.height(72.dp)); Text("¿Sigue el problema? Escríbenos y te ayudamos con\ntu cuenta, tus pedidos o la aplicación.", Modifier.padding(horizontal = 36.dp), fontSize = 12.sp); Spacer(Modifier.height(15.dp)); BlackButton("Contactar a soporte", Modifier.fillMaxWidth().padding(horizontal = 41.dp)) {}
    }
}

@Composable
private fun Sync(go: (Screen) -> Unit) = Page(Screen.Sync, go) {
    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        Header("Sincronización") { go(Screen.Settings) }
        Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(5.dp)).background(Color(0xFFECECEC)).padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Text("⌁", Modifier.size(40.dp).clip(RoundedCornerShape(6.dp)).background(Color(0xFFD5F6D5)).padding(6.dp), color = Color(0xFF23863B), fontSize = 26.sp); Column(Modifier.padding(start = 16.dp)) { Text("Conexión disponible", fontWeight = FontWeight.SemiBold, fontSize = 14.sp); Text("Puedes sincronizar tus cambios ahora", fontSize = 11.sp) } }
        Spacer(Modifier.height(23.dp)); Text("Información guardada", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        listOf("Catálogo de artesanos", "Favoritos", "Pedidos recientes", "Cotizaciones").forEach { Status("✓", it, "Sin conexión", Green) }
        Spacer(Modifier.height(16.dp)); Text("Cambios pendientes", fontSize = 15.sp, fontWeight = FontWeight.Bold); Status("●", "Pedido actualizado", "En espera", Amber); Status("●", "Producto agregado a favoritos", "En espera", Amber)
        Spacer(Modifier.weight(1f)); BlackButton("Sincronizar ahora", Modifier.fillMaxWidth().padding(bottom = 18.dp)) {}
    }
}

@Composable
private fun Favorites(go: (Screen) -> Unit, openProduct: (Product) -> Unit) = Page(Screen.Favorites, go) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) { Header("♥ Favoritos", Modifier.padding(start = 25.dp)) { go(Screen.Home) }; Tabs(listOf("Productos", "Productores", "Novedades"), 0); Grid(listOf(Retablo, Alpaca), openProduct, Modifier.padding(25.dp, 30.dp)) }
}

@Composable
private fun About(go: (Screen) -> Unit) = Page(Screen.About, go) {
    var showCredits by rememberSaveable { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Header("Acerca de Tinkuy") { go(Screen.Settings) }
        Box(Modifier.size(60.dp).clip(RoundedCornerShape(6.dp)).background(Green), contentAlignment = Alignment.Center) { Text("T", color = Color.White, fontSize = 35.sp, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(12.dp)); Text("Tinkuy", fontWeight = FontWeight.Bold, fontSize = 14.sp); Spacer(Modifier.height(10.dp)); Text("Marketplace que conecta a compradores", color = Color(0xFF5D5D5D), fontSize = 11.sp); Spacer(Modifier.height(9.dp)); Text("con artesanos, y te permite consultar tu", color = Color(0xFF5D5D5D), fontSize = 11.sp); Spacer(Modifier.height(9.dp)); Text("información aun sin conexión.", color = Color(0xFF5D5D5D), fontSize = 11.sp); Spacer(Modifier.height(36.dp))
        InfoPair("Versión", "1.0.0"); InfoPair("Perfil actual", "Comprador"); InfoPair("Modo sin conexión", "Disponible"); Menu("", "Términos y condiciones", 0.dp) {}; Menu("", "Políticas de privacidad", 0.dp) {}
        Menu("", "Créditos de las fotografías", 0.dp) { showCredits = true }
    }
    if (showCredits) PhotoCredits { showCredits = false }
}

private data class PhotoCredit(val author: String, val source: String, val license: String, val licenseUrl: String)

private val photoCredits = listOf(
    PhotoCredit("Eugenia Remark · Suéter crema",
        "https://www.pexels.com/photo/a-cream-sweater-hanging-on-a-clothes-hanger-13918752/",
        "Licencia de Pexels", "https://www.pexels.com/license/"),
    PhotoCredit("Jorgeluishp · Cerámica de Chulucanas",
        "https://commons.wikimedia.org/wiki/File:Ceramica-chulucanas.jpg",
        "CC BY-SA 4.0", "https://creativecommons.org/licenses/by-sa/4.0/"),
    PhotoCredit("Anna.Massini · Suéter tejido a mano",
        "https://commons.wikimedia.org/wiki/File:Maglione_realizzato_con_i_ferri.jpg",
        "CC BY 4.0", "https://creativecommons.org/licenses/by/4.0/"),
    PhotoCredit("EduardoZambrano · Retablo ayacuchano",
        "https://commons.wikimedia.org/wiki/File:Retablo_ayacuchano_(38621536495).jpg",
        "CC BY-SA 2.0", "https://creativecommons.org/licenses/by-sa/2.0/"),
    PhotoCredit("Annerella · Café peruano en grano",
        "https://commons.wikimedia.org/wiki/File:Peruvian_Coffee_Beans.jpg",
        "CC BY-SA 3.0", "https://creativecommons.org/licenses/by-sa/3.0/")
)

@Composable
private fun PhotoCredits(dismiss: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    AlertDialog(
        onDismissRequest = dismiss,
        title = { Text("Créditos de las fotografías") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Text("Imágenes referenciales. Se muestran recortadas para ajustarse a la pantalla.")
                photoCredits.forEach { credit ->
                    HorizontalDivider(Modifier.padding(vertical = 8.dp), color = Line)
                    Text(credit.author)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        TextButton(onClick = { uriHandler.openUri(credit.source) }) { Text("Fuente") }
                        TextButton(onClick = { uriHandler.openUri(credit.licenseUrl) }) { Text(credit.license) }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = dismiss) { Text("Cerrar") } }
    )
}

@Composable
private fun Requests(go: (Screen) -> Unit) = Page(Screen.Requests, go) {
    Column(Modifier.fillMaxSize()) { Header("Solicitudes de cotización") { go(Screen.History) }; Request(Alpaca, "Confirmado", "4 unid", Green); Request(Sheep, "Pendiente", "2 unid", Blue); Request(Retablo, "", "", Muted) }
}

private val demoProposals = listOf(
    ShopOrderItem(Alpaca, 4),
    ShopOrderItem(Sheep, 2),
    ShopOrderItem(Retablo, 2)
)

@Composable
private fun Proposals(
    go: (Screen) -> Unit,
    handled: List<String>,
    onAccept: (ShopOrderItem) -> Unit,
    onReject: (ShopOrderItem) -> Unit
) = Page(Screen.Proposals, go) {
    var selected by rememberSaveable { mutableStateOf<ShopOrderItem?>(null) }
    val proposals = demoProposals.filter { it.product.name !in handled }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Header("Propuestas recibidas") { go(Screen.History) }
        if (proposals.isEmpty()) {
            Text("No tienes propuestas por revisar.", Modifier.padding(24.dp), color = Muted)
        }
        proposals.forEach { item ->
            Proposal(item, { selected = item }, { onAccept(item) }, { onReject(item) })
        }
    }
    selected?.let { item ->
        AlertDialog(
            onDismissRequest = { selected = null },
            title = { Text(item.product.title) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(item.product.maker)
                    Text("Cantidad: " + item.quantity)
                    Text("Precio por unidad: " + soles(item.unitPrice))
                    Text("Total: " + soles(item.subtotal), fontWeight = FontWeight.SemiBold)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    selected = null
                    onAccept(item)
                }) { Text("Aceptar propuesta") }
            },
            dismissButton = { TextButton(onClick = { selected = null }) { Text("Cerrar") } }
        )
    }
}

@Composable
internal fun BlackButton(label: String, modifier: Modifier = Modifier, action: () -> Unit) {
    Button(action, modifier.heightIn(min = 48.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Color.White)) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun Field(label: String, value: String, change: (String) -> Unit, modifier: Modifier = Modifier.fillMaxWidth(), hint: String = "", key: KeyboardType = KeyboardType.Text, single: Boolean = true, readOnly: Boolean = false) {
    OutlinedTextField(
        value, change, modifier,
        label = { Text(label, fontSize = 13.sp) },
        placeholder = { if (hint.isNotEmpty()) Text(hint, color = Color(0xFF77727E)) },
        singleLine = single,
        readOnly = readOnly,
        keyboardOptions = KeyboardOptions(keyboardType = key),
        visualTransformation = if (key == KeyboardType.Password) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        shape = RoundedCornerShape(8.dp),
        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF55515A),
            unfocusedBorderColor = Color(0xFF706B74),
            focusedLabelColor = Ink
        )
    )
}

@Composable
private fun Tabs(labels: List<String>, selected: Int) {
    Row(Modifier.fillMaxWidth().height(47.dp), horizontalArrangement = Arrangement.SpaceEvenly) { labels.forEachIndexed { index, label -> Text(label, Modifier.weight(1f).padding(top = 13.dp), color = if (index == selected) Ink else Muted, fontSize = 14.sp, textAlign = TextAlign.Center) } }
    Row(Modifier.fillMaxWidth()) { Box(Modifier.weight(1f / labels.size).height(4.dp).background(Color.Black)); Spacer(Modifier.weight(1f - 1f / labels.size)) }; Divider(color = Line)
}


@Composable
private fun CatalogRow(product: Product, open: () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        Modifier.fillMaxWidth().clip(shape).background(Color.White).border(1.dp, Line, shape)
            .clickable(onClick = open).padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Thumb(product, Modifier.size(72.dp).clip(RoundedCornerShape(10.dp)))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(product.title, color = Ink, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Text(product.maker, color = Muted, fontSize = 13.sp)
            Text(product.price, color = Accent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        TinkuyIcon(AppIcon.ChevronRight, Modifier.size(18.dp), Muted)
    }
}

@Composable
private fun Placeholder(modifier: Modifier, pale: Boolean = false) {
    Canvas(modifier) {
        val color = if (pale) Color(0xFFD8D8D8) else Color(0xFF222222); val inset = size.minDimension * .2f; val side = size.minDimension - inset * 2
        drawRect(color, Offset(inset, inset), Size(side, side), style = Stroke(width = if (pale) 1f else 1.5f)); drawLine(color, Offset(inset, inset), Offset(inset + side, inset + side), strokeWidth = if (pale) 1f else 1.5f); drawLine(color, Offset(inset + side, inset), Offset(inset, inset + side), strokeWidth = if (pale) 1f else 1.5f)
    }
}

@Composable
private fun Avatar(modifier: Modifier, person: Boolean = true) {
    Box(modifier.clip(CircleShape).background(Color(0xFFDCE0E4)), contentAlignment = Alignment.Center) {
        if (person) Canvas(Modifier.fillMaxSize()) { drawCircle(Color.White, size.minDimension * .18f, Offset(size.width / 2, size.height * .35f)); drawCircle(Color.White, size.minDimension * .34f, Offset(size.width / 2, size.height * .87f)) } else Placeholder(Modifier.fillMaxSize().padding(29.dp), true)
    }
}
@Composable
private fun CartLine(product: Product, count: Int, down: () -> Unit, up: () -> Unit) {
    Row(Modifier.fillMaxWidth().height(113.dp), verticalAlignment = Alignment.CenterVertically) {
        Thumb(product, Modifier.size(86.dp))
        Column(Modifier.padding(start = 16.dp).weight(1f)) {
            Text(product.title, fontSize = 14.sp)
            Text(product.price, fontSize = 13.sp, color = Muted)
        }
        Row(Modifier.padding(end = 17.dp), verticalAlignment = Alignment.CenterVertically) {
            CountButton("−", down); Text("$count", Modifier.padding(horizontal = 13.dp), fontSize = 16.sp); CountButton("+", up)
        }
    }
}

@Composable
private fun CountButton(label: String, action: () -> Unit) = Text(label, Modifier.size(27.dp).background(Color(0xFFF3F3F3)).padding(top = 1.dp).clickable { action() }, fontSize = 21.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)

@Composable
private fun Success(title: String, text: String, actionLabel: String, action: () -> Unit) {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(title, fontSize = 26.sp); Spacer(Modifier.height(20.dp)); Box(Modifier.size(101.dp).clip(RoundedCornerShape(11.dp)).background(Color.Black), contentAlignment = Alignment.Center) { Text("✓", Modifier.padding(bottom = 13.dp), color = Color.White, fontSize = 85.sp) }
        Spacer(Modifier.height(23.dp)); Text(text, color = Color(0xFF6C6C6C), fontSize = 13.sp, lineHeight = 24.sp, textAlign = TextAlign.Center); Spacer(Modifier.height(18.dp)); BlackButton(actionLabel, Modifier.width(214.dp), action)
    }
}

@Composable
private fun Menu(icon: String, label: String, margin: Dp = 33.dp, action: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = margin).heightIn(min = 58.dp).clickable(onClick = action),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val vector = when (icon) {
            "♙" -> AppIcon.Profile
            "▤" -> AppIcon.Package
            "♟" -> AppIcon.Bell
            else -> null
        }
        if (vector != null) {
            TinkuyIcon(vector, tint = Accent)
            Spacer(Modifier.width(14.dp))
        } else if (icon.isNotEmpty()) {
            Text(icon, Modifier.width(38.dp), color = Accent, fontSize = 22.sp)
        }
        Text(label, Modifier.weight(1f), color = Ink, fontSize = 16.sp)
        TinkuyIcon(AppIcon.ChevronRight, Modifier.size(18.dp), Muted)
    }
    HorizontalDivider(Modifier.padding(horizontal = margin), color = Line)
}

@Composable
private fun HelpLine(icon: String, title: String, detail: String) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 34.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(icon, Modifier.size(36.dp).clip(RoundedCornerShape(7.dp)).background(Color(0xFFE1E1E1)).padding(top = 4.dp), fontSize = 25.sp, textAlign = TextAlign.Center)
        Column(Modifier.padding(start = 10.dp)) { Text(title, fontSize = 16.sp); Text(detail, fontSize = 13.sp, fontStyle = FontStyle.Italic, lineHeight = 20.sp) }
    }; Divider(Modifier.padding(horizontal = 31.dp), color = Line)
}

@Composable
private fun Status(icon: String, label: String, status: String, color: Color) = Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
    Text(icon, Modifier.width(28.dp), color = color, fontSize = 18.sp); Text(label, Modifier.weight(1f), fontSize = 13.sp); Text(status, color = Color(0xFF767676), fontSize = 11.sp)
}

@Composable
private fun InfoPair(label: String, value: String) = Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
    Text(label, fontSize = 16.sp); Text(value, color = Color(0xFF656565), fontSize = 16.sp)
}

@Composable
private fun Request(product: Product, status: String, qty: String, color: Color) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 35.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Thumb(product, Modifier.size(68.dp)); Column(Modifier.padding(start = 15.dp).weight(1f)) { Text(product.title, fontSize = 17.sp); Text(product.maker, color = Muted, fontSize = 12.sp) }
        Column(horizontalAlignment = Alignment.End) { if (status.isNotBlank()) Text(status, color = color, fontSize = 16.sp); if (qty.isNotBlank()) Text(qty, fontSize = 16.sp) }
    }; Divider(Modifier.padding(horizontal = 42.dp), color = Line)
}

@Composable
private fun Proposal(item: ShopOrderItem, open: () -> Unit, accept: () -> Unit, reject: () -> Unit) {
    val product = item.product
    Column(Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Thumb(product, Modifier.size(69.dp).clip(RoundedCornerShape(12.dp)))
            Column(Modifier.weight(1f)) {
                Text(product.title, fontSize = 17.sp)
                Text(product.maker, color = Muted, fontSize = 12.sp)
                Text(item.quantity.toString() + " unid.", fontSize = 14.sp)
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            BlackButton("Confirmar", Modifier.weight(1f), accept)
            OutlinedButton(onClick = reject, modifier = Modifier.weight(1f).height(47.dp)) { Text("Rechazar") }
        }
        TextButton(onClick = open, modifier = Modifier.align(Alignment.End)) { Text("Ver detalle") }
        HorizontalDivider(Modifier.padding(top = 8.dp), color = Line)
    }
}
