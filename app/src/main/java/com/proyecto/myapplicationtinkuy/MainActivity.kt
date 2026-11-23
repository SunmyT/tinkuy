package com.proyecto.myapplicationtinkuy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
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

internal val Ink = Color(0xFF1D2723)
internal val Muted = Color(0xFF586761)
internal val Line = Color(0xFFDFE6E2)
internal val AppBackground = Color(0xFFF7F8F6)
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
        Screen.Register -> Register(go)
        Screen.Password -> Password(go)
        Screen.Verify -> Verify(go)
        Screen.Forgot -> Forgot(go)
        Screen.Home -> Home(go, homeCategory, { homeCategory = it }, homeQuery, { homeQuery = it }, openProduct)
        Screen.Catalog -> Catalog(go, catalogCategory, { catalogCategory = it }, catalogQuery, { catalogQuery = it }, openProduct)
        Screen.Detail -> Detail(product, go, back) { changeQuantity(product, 1) }
        Screen.Producer -> Producer(product, go, openProduct)
        Screen.Cart -> if (quantities.any { it > 0 }) Cart(go, quantities, changeQuantity, confirmCart) else EmptyCart(go)
        Screen.OrderDone -> OrderDone(go)
        Screen.Quote -> Quote(product, go)
        Screen.QuoteDone -> QuoteDone(product, go)
        Screen.Orders -> OrdersScreen(orders, orderFilter, { orderFilter = it }, openOrder, go, back)
        Screen.Tracking -> TrackingScreen(orders.firstOrNull { it.id == selectedOrderId } ?: orders.first(), go, back)
        Screen.Profile -> Profile(go)
        Screen.Account -> Account(go)
        Screen.History -> History(go)
        Screen.Notifications -> Notifications(go, back)
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
internal fun Page(screen: Screen, go: (Screen) -> Unit, bottom: Boolean = true, body: @Composable BoxScope.() -> Unit) {
    val keyboardVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    Column(Modifier.fillMaxSize().background(AppBackground)) {
        Box(Modifier.weight(1f), content = body)
        if (bottom && !keyboardVisible) BottomNav(screen, go)
    }
}

@Composable
private fun BottomNav(screen: Screen, go: (Screen) -> Unit) {
    val choices = listOf(
        Triple(Screen.Home, "Inicio", AppIcon.Home),
        Triple(Screen.Catalog, "Catálogo", AppIcon.Search),
        Triple(Screen.Cart, "Carrito", AppIcon.Cart),
        Triple(Screen.Profile, "Perfil", AppIcon.Profile)
    )
    HorizontalDivider(color = Line)
    Row(Modifier.fillMaxWidth().background(Color.White).selectableGroup().padding(horizontal = 8.dp)) {
        choices.forEach { (destination, label, icon) ->
            val selected = when (destination) {
                Screen.Home -> screen == Screen.Home
                Screen.Catalog -> screen in listOf(Screen.Catalog, Screen.Detail, Screen.Producer, Screen.Favorites)
                Screen.Cart -> screen in listOf(Screen.Cart, Screen.OrderDone)
                else -> screen in listOf(Screen.Profile, Screen.Account, Screen.History, Screen.Notifications, Screen.Settings, Screen.Help, Screen.Sync, Screen.About, Screen.Orders, Screen.Tracking, Screen.Requests, Screen.Proposals)
            }
            val tint = if (selected) Accent else Muted
            Column(
                Modifier.weight(1f).heightIn(min = 64.dp).clip(RoundedCornerShape(14.dp))
                    .selectable(selected = selected, role = Role.Tab, onClick = { go(destination) })
                    .semantics { contentDescription = label }.padding(vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    Modifier.size(48.dp, 30.dp).background(if (selected) AccentSoft else Color.Transparent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    TinkuyIcon(icon, Modifier.size(21.dp), tint)
                }
                Text(label, color = tint, fontSize = 12.sp, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun Welcome(go: (Screen) -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Box(Modifier.weight(1f).fillMaxWidth().background(Color(0xFFF4F4F4)), contentAlignment = Alignment.Center) { Placeholder(Modifier.size(64.dp), true) }
        Column(Modifier.padding(17.dp, 42.dp)) {
            Text("TINKUY", fontSize = 36.sp, letterSpacing = 1.sp)
            Text("Conecta. Descubre. Compra.", fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(21.dp))
            Text("Explora productos, solicita cotizaciones y\nconecta directamente con productores", color = Muted, fontSize = 16.sp, lineHeight = 23.sp)
            Spacer(Modifier.height(37.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BlackButton("Iniciar Sesión", Modifier.weight(1f)) { go(Screen.Login) }
                BlackButton("Crear Cuenta", Modifier.weight(1f)) { go(Screen.Register) }
            }
        }
    }
}

@Composable
private fun Login(go: (Screen) -> Unit) {
    var email by rememberSaveable { mutableStateOf("") }; var password by rememberSaveable { mutableStateOf("") }
    Form(false, {}) {
        Spacer(Modifier.height(46.dp)); Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { Avatar(Modifier.size(174.dp), false) }
        Spacer(Modifier.height(42.dp))
        Field("Correo electrónico", email, { email = it }, key = KeyboardType.Email)
        Error("Por favor ingresa un correo válido"); Spacer(Modifier.height(15.dp))
        Field("Contraseña", password, { password = it }, key = KeyboardType.Password)
        Error("Por favor ingresa una contraseña válida")
        Text("¿Has olvidado la contraseña?", Modifier.fillMaxWidth().clickable { go(Screen.Forgot) }, textAlign = TextAlign.Center, fontSize = 12.sp, textDecoration = TextDecoration.Underline)
        Spacer(Modifier.height(9.dp)); BlackButton("Ingresar", Modifier.fillMaxWidth()) { go(Screen.Home) }
        Spacer(Modifier.weight(1f))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text("¿No tienes una cuenta?  ", fontSize = 14.sp); Text("Regístrate", fontSize = 14.sp, textDecoration = TextDecoration.Underline, modifier = Modifier.clickable { go(Screen.Register) })
        }
        Spacer(Modifier.height(44.dp))
    }
}

@Composable
private fun Register(go: (Screen) -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }; var surname by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }; var phone by rememberSaveable { mutableStateOf("") }
    Form(true, { go(Screen.Welcome) }) {
        Spacer(Modifier.height(20.dp)); Text("Registro de usuario", fontSize = 26.sp); Spacer(Modifier.height(17.dp))
        ValidField("Nombres", "Ingresa tus apellidos", name, { name = it }, "Por favor ingresa un nombre válido")
        Spacer(Modifier.height(9.dp)); ValidField("Apellidos", "Ingresa tus apellidos", surname, { surname = it }, "Por favor ingresa un apellido válido")
        Spacer(Modifier.height(9.dp)); ValidField("Correo electrónico", "ejemplo@correo.com", email, { email = it }, "Por favor ingresa un correo válido")
        Spacer(Modifier.height(9.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { Field("Code", "+44", {}, Modifier.width(83.dp), readOnly = true); Field("Teléfono", phone, { phone = it }, Modifier.weight(1f), "999999999", KeyboardType.Phone) }
        Error("Por favor ingresa un número de teléfono válido"); Spacer(Modifier.height(10.dp))
        BlackButton("Siguiente", Modifier.fillMaxWidth()) { go(Screen.Password) }
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun Password(go: (Screen) -> Unit) {
    var password by rememberSaveable { mutableStateOf("") }; var repeat by rememberSaveable { mutableStateOf("") }
    Form(true, { go(Screen.Register) }) {
        Spacer(Modifier.height(36.dp)); Text("Crea tu contraseña", fontSize = 26.sp); Spacer(Modifier.height(23.dp))
        Field("Contraseña", password, { password = it }, key = KeyboardType.Password)
        Text("Contraseña segura", Modifier.padding(start = 8.dp, top = 6.dp), color = Green, fontSize = 12.sp)
        Row(Modifier.fillMaxWidth().padding(8.dp, 7.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) { repeat(3) { Box(Modifier.weight(1f).height(5.dp).clip(CircleShape).background(Color(0xFF78E08F))) } }
        Field("Repetir contraseña", repeat, { repeat = it }, key = KeyboardType.Password)
        Text("Las contraseñas deben ser iguales", Modifier.padding(start = 8.dp, top = 4.dp), color = Color(0xFFFF6D36), fontSize = 12.sp)
        Spacer(Modifier.height(24.dp)); BlackButton("Crear Cuenta", Modifier.fillMaxWidth()) { go(Screen.Verify) }; Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun Verify(go: (Screen) -> Unit) {
    var code by rememberSaveable { mutableStateOf(List(5) { "" }) }
    Form(true, { go(Screen.Password) }) {
        Spacer(Modifier.height(98.dp)); Text("Verifica tu número", fontSize = 26.sp); Text("Hemos enviado un código de verificación\npor SMS", color = Muted, fontSize = 16.sp, lineHeight = 23.sp)
        Spacer(Modifier.height(47.dp)); Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            code.indices.forEach { i -> OutlinedTextField(code[i], { v -> code = code.toMutableList().also { it[i] = v.take(1) } }, Modifier.weight(1f).height(57.dp), singleLine = true, textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center), shape = RoundedCornerShape(5.dp)) }
        }
        Spacer(Modifier.height(36.dp)); Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) { Text("¿No recibiste el código?  ", fontSize = 14.sp); Text("Reenviar código", fontSize = 14.sp, textDecoration = TextDecoration.Underline) }
        Spacer(Modifier.height(58.dp)); BlackButton("Verificar", Modifier.fillMaxWidth().padding(horizontal = 30.dp)) { go(Screen.Home) }; Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun Forgot(go: (Screen) -> Unit) {
    var email by rememberSaveable { mutableStateOf("") }
    Form(true, { go(Screen.Login) }) {
        Spacer(Modifier.height(110.dp)); Text("Recupera tu contraseña", fontSize = 26.sp); Spacer(Modifier.height(12.dp))
        Text("Ingresa tu correo electrónico y te enviaremos instrucciones para recuperar tu acceso.", color = Muted, fontSize = 16.sp, lineHeight = 23.sp)
        Spacer(Modifier.height(32.dp)); Field("Correo electrónico", email, { email = it }, key = KeyboardType.Email); Spacer(Modifier.height(22.dp))
        BlackButton("Enviar instrucciones", Modifier.fillMaxWidth()) { go(Screen.Login) }; Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun Home(
    go: (Screen) -> Unit,
    category: Category,
    selectCategory: (Category) -> Unit,
    query: String,
    onQueryChange: (String) -> Unit,
    openProduct: (Product) -> Unit
) = Page(Screen.Home, go) {
    val focus = LocalFocusManager.current
    val products = remember(category, query) { category.products(query) }
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(top = 22.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Buenas tardes", color = Muted, fontSize = 15.sp)
                Text("Usuario", color = Ink, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                IconButton(onClick = { go(Screen.Favorites) }, modifier = Modifier.background(Color.White, CircleShape)) {
                    TinkuyIcon(AppIcon.Heart, contentDescription = "Favoritos", tint = Accent)
                }
                IconButton(onClick = { go(Screen.Notifications) }, modifier = Modifier.background(Color.White, CircleShape)) {
                    TinkuyIcon(AppIcon.Bell, contentDescription = "Notificaciones", tint = Accent)
                }
            }
        }
        Search(query, onQueryChange)
        CategoryChips(category, {
            focus.clearFocus()
            selectCategory(it)
        })
        Text(
            if (query.isBlank()) "Productos destacados" else "Resultados de búsqueda",
            color = Ink, fontSize = 21.sp, fontWeight = FontWeight.SemiBold
        )
        if (products.isEmpty()) {
            EmptySearch { onQueryChange("") }
        } else {
            Grid(products, { product ->
                focus.clearFocus()
                openProduct(product)
            })
        }
        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun Catalog(
    go: (Screen) -> Unit,
    category: Category,
    selectCategory: (Category) -> Unit,
    query: String,
    onQueryChange: (String) -> Unit,
    openProduct: (Product) -> Unit
) = Page(Screen.Catalog, go) {
    val focus = LocalFocusManager.current
    val products = remember(category, query) { category.products(query) }
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Catálogo", color = Ink, fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Search(query, onQueryChange)
        CategoryChips(category, {
            focus.clearFocus()
            selectCategory(it)
        })
        if (products.isEmpty()) {
            EmptySearch { onQueryChange("") }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                products.forEach { product ->
                    CatalogRow(product) {
                        focus.clearFocus()
                        openProduct(product)
                    }
                }
            }
        }
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
private fun Form(back: Boolean, onBack: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
                .heightIn(min = maxHeight).padding(horizontal = 25.dp)
        ) {
            if (back) {
                IconButton(onClick = onBack, modifier = Modifier.padding(top = 8.dp)) {
                    TinkuyIcon(AppIcon.Back, tint = Ink, contentDescription = "Volver")
                }
            }
            content()
        }
    }
}

@Composable
internal fun Header(title: String, modifier: Modifier = Modifier, back: () -> Unit) {
    Row(
        modifier.fillMaxWidth().heightIn(min = 68.dp).padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = back) { TinkuyIcon(AppIcon.Back, contentDescription = "Volver", tint = Ink) }
        Text(
            title, Modifier.weight(1f), textAlign = TextAlign.Center,
            color = Ink, fontWeight = FontWeight.SemiBold, fontSize = 23.sp,
            maxLines = 2, overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.width(48.dp))
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
private fun ValidField(label: String, hint: String, value: String, set: (String) -> Unit, error: String) {
    Field(label, value, set, hint = hint); Error(error)
}

@Composable
private fun Error(text: String) = Text(text, Modifier.padding(start = 5.dp, top = 3.dp), color = Color(0xFFFF4040), fontSize = 12.sp)

@Composable
private fun Search(query: String, onQueryChange: (String) -> Unit) {
    val focus = LocalFocusManager.current
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Buscar productos" },
        placeholder = { Text("Buscar productos", color = Muted) },
        leadingIcon = { TinkuyIcon(AppIcon.Search, tint = Accent) },
        trailingIcon = if (query.isNotEmpty()) {
            {
                IconButton(onClick = { onQueryChange("") }) {
                    TinkuyIcon(AppIcon.Close, tint = Muted, contentDescription = "Borrar búsqueda")
                }
            }
        } else null,
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { focus.clearFocus() }),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Accent,
            unfocusedBorderColor = Line,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            cursorColor = Accent
        )
    )
}

@Composable
private fun EmptySearch(clearQuery: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(18.dp))
            .border(1.dp, Line, RoundedCornerShape(18.dp)).padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        TinkuyIcon(AppIcon.Search, tint = Accent)
        Text("No encontramos productos", fontWeight = FontWeight.SemiBold, color = Ink)
        Text("Prueba otro nombre o cambia de categoría.", color = Muted, textAlign = TextAlign.Center)
        TextButton(onClick = clearQuery, colors = ButtonDefaults.textButtonColors(contentColor = Accent)) {
            Text("Borrar búsqueda")
        }
    }
}

@Composable
private fun CategoryChips(category: Category, onSelect: (Category) -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier.selectableGroup().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Category.entries.forEach { item ->
            val selected = category == item
            Box(
                Modifier.heightIn(min = 48.dp).clip(shape)
                    .background(if (selected) Accent else Color.White)
                    .border(1.dp, if (selected) Accent else Line, shape)
                    .selectable(selected = selected, role = Role.Tab, onClick = { onSelect(item) })
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(item.label, color = if (selected) Color.White else Ink, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun Tabs(labels: List<String>, selected: Int) {
    Row(Modifier.fillMaxWidth().height(47.dp), horizontalArrangement = Arrangement.SpaceEvenly) { labels.forEachIndexed { index, label -> Text(label, Modifier.weight(1f).padding(top = 13.dp), color = if (index == selected) Ink else Muted, fontSize = 14.sp, textAlign = TextAlign.Center) } }
    Row(Modifier.fillMaxWidth()) { Box(Modifier.weight(1f / labels.size).height(4.dp).background(Color.Black)); Spacer(Modifier.weight(1f - 1f / labels.size)) }; Divider(color = Line)
}

@Composable
private fun Grid(products: List<Product>, openProduct: (Product) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        products.chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { product -> Card(product, Modifier.weight(1f)) { openProduct(product) } }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun Card(product: Product, modifier: Modifier, open: () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Column(modifier.clip(shape).background(Color.White).border(1.dp, Line, shape).clickable(onClick = open)) {
        Thumb(product, Modifier.fillMaxWidth().aspectRatio(1.12f))
        Column(Modifier.padding(11.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(product.title, color = Ink, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, minLines = 2, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(product.maker, color = Muted, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(product.price, Modifier.padding(top = 3.dp), color = Accent, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }
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
internal fun Thumb(product: Product, modifier: Modifier) {
    Image(painterResource(product.photo), contentDescription = null, modifier = modifier, contentScale = ContentScale.Crop)
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
