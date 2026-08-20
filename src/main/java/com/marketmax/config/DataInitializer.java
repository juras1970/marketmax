package com.marketmax.config;

import com.marketmax.model.*;
import com.marketmax.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final NotificationRepository notificationRepository;
    private final CartItemRepository cartItemRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        seedCategories();

        if (productRepository.count() > 0) {
            log.info("Banco de dados já inicializado. Pulando seed de produtos/usuários.");
            return;
        }

        log.info("Inicializando banco de dados com dados de seed...");

        // =====================
        // PRODUTOS
        // =====================

        // 1. Câmera Profissional
        Product camera = createProduct("camera-1",
            "Câmera Profissional UltraVision X1 - Edição Premium 4K",
            new BigDecimal("3899.00"), new BigDecimal("4599.00"), 15,
            "eletro", true,
            "A UltraVision X1 redefine o que é possível na fotografia digital. Equipada com um sensor Full-Frame de última geração e processamento de imagem alimentado por IA, esta câmera captura detalhes que o olho humano mal consegue perceber.\n\nIdeal para criadores de conteúdo exigentes, profissionais de eventos e entusiastas que buscam a perfeição técnica. Seu corpo em liga de magnésio garante durabilidade extrema em qualquer ambiente, enquanto a interface intuitiva permite que você se concentre no que realmente importa: sua arte.",
            new BigDecimal("4.8"), "+1000 vendidos", true,
            "Líder Platinum", "4.8/5", "+50mil", "< 24h",
            Arrays.asList(
                "https://lh3.googleusercontent.com/aida-public/AB6AXuBfY1lDfpHD-FyhdSd9cNlFEvxvtg9tROGSoNPeMDJa_TSbZokNPjKQZ75wl9iHIeQw2HtJksAdpbB_6-A5gRaUhkUvbKdl-EWqX3zvUjXGkTL8ZQXc9e2HweiUDOa3IiLNHWfI-_vbgoqBpNvkUwykyq1-fS3tgMJAcMIhanrmzs4d7WWbMPBrHSxVx9LB2K6Dnn3HmIeIVEKl46jyPmPVC6JmDPf67pgbkY40XUDvfgZcHQLYJuQq3NbVz2FNt3k2ha72Kx6vaEBq",
                "https://lh3.googleusercontent.com/aida-public/AB6AXuAAJWGh_olPV9NYK7R2t11iaUJ5UYi32IPwnsCAc1Jm5xDGQqgyFZ1G-edhx_Gp5_Wq3R5C_YM9gZrCI6_VmgwfF0g4gwojazgNLxdbhbK029l-Mn8M8ZlT6oXyNCBehWryGmCMrN2LscgeaVokjXGhp8noZUxIqZdR3o7YugpWVfjQNDip1d_nCWmysMPUfL-fvZ-SKchNC_L-3PEt69wEM3gTK3msDXYUgh47cFX5LG7UO8TL_4i6mDkKmlQaVjwfKul81SnngNdK",
                "https://lh3.googleusercontent.com/aida-public/AB6AXuDQ9U7BNd_C6PNWbuzBNKbqUQFgw9MYQtIKp7MhdYBY2Ud9Wm70zCnHFiR2T4Z-EJUJQ7oAh--PQRc1VMr0izZkO7W-UrmI27LwJR3TlIetb3Zll3z1fJRmKp5tv53V6171jnVKCf8moVFHey6mhL2FibqA5n5sDXWwFJsDhvPpDrkj8MjMjCTPW0YlnycQFHmAcx1JvDKLYSkr7j-DUZqSzXLoyvCVoXu71swquCDoXIt0f-CfKHe1YiDlSR-wfl8b4nmN0b7gMIhQ"
            ),
            Arrays.asList(
                new String[]{"Resolução", "45.7 Megapixels"},
                new String[]{"Vídeo", "8K a 60fps / 4K a 120fps"},
                new String[]{"ISO", "64 - 25.600 (Expansível)"},
                new String[]{"Peso", "705g (Apenas corpo)"},
                new String[]{"Conectividade", "Wi-Fi, Bluetooth, USB-C"}
            )
        );

        // 2. Smartwatch
        Product smartwatch = createProduct("smartwatch-1",
            "Smartwatch Pro 2024 Series X",
            new BigDecimal("899.90"), new BigDecimal("1200.00"), 25,
            "eletro", true,
            "Monitore sua saúde e atividades com precisão de elite. O Smartwatch Pro 2024 possui display OLED Always-On de altíssimo brilho, GPS multi-banda aprimorado e resistência total à água de até 50 metros.",
            new BigDecimal("4.7"), "+200 vendidos", false,
            null, null, null, null,
            Arrays.asList(
                "https://lh3.googleusercontent.com/aida-public/AB6AXuAJktFQzTSmYkIxWPe_gnDfOfxuFJUTWOW51DmOMitOpUvUUAcMNdaDZRq9Do6zAt9oRoQipZ0DeWzvDdY4DdlR_4emQo7JlwTub2EHbhvhZSFmdZGxuIf6OdWH6HHTbRS5AWuaCR90ZyyoSX_5Ta7k64nyFuT_lZD2dJsM2hf5YtDcNKEAYugmFoBLPJm3tWKWG0SqOHxjhn5HamvrAGXkaWJF5irel1Vxd4SZDp44hTap0SzkuVwpmqQcs5l62nxaN62u0VGlX435"
            ),
            Arrays.asList(
                new String[]{"Display", "1.95 polegadas OLED"},
                new String[]{"Bateria", "Até 7 dias de uso intenso"},
                new String[]{"Resistência", "5 ATM IP68"},
                new String[]{"Sensores", "Frequência cardíaca, SpO2, ECG"}
            )
        );

        // 3. Headphone
        Product headphone = createProduct("headphone-1",
            "Headphone Premium Wireless Noise Cancelling",
            new BigDecimal("382.50"), new BigDecimal("450.00"), 15,
            "games", true,
            "Som cristalino com cancelamento de ruído ativo de última geração. O Headphone Premium Wireless oferece até 40 horas de reprodução contínua e almofadas ergonômicas de espuma viscoelástica para máximo conforto.",
            new BigDecimal("4.9"), "+5000 vendidos", false,
            "Líder Platinum", null, null, null,
            Arrays.asList(
                "https://lh3.googleusercontent.com/aida-public/AB6AXuAtjOs6Ww3NC2rD-EG6B4SzcQbGbSvoi0ew6x9xRGlYlguYrqoDknIQwRX8wHiX41LuHloh24wIHevI-srRqNNLFxU-qbptX6YRg0Op0l3HLoQZqtFdutHzYrYiNBpXi5RaQgADC7ztfbKX8JjfBHIBDODcCbva1o80fhMof6rg_6iZRApMCXdOKwin2PDnz4oW9ko_7U_bkyoa8So5d1e07O_qPr-AWHO2-Gi1ziudEEvepWqyFgGwPpDpxQ7daxyninDkv9Txr7WL",
                "https://lh3.googleusercontent.com/aida-public/AB6AXuDkq5Oxx3T8hR2hPIZgOiSqLHljhA5EBBRtsLUmK4LinAsKru8bjbRNeTwuJqnjIAv1WJQxo7CjuKexN0AIdnRfr49I9y5PFRzUSG-qWZDqoFFWGnGxpYdz3t6Hlur9TdPlass3VEYczHniIgx60f0JDyfDtbDO9fjOjtFQK_BYsHA_f5j8zstDeNJdOGqUvldvGcZMCRC8bKLqX6AX-LeWpJv41xgkyOMV5Z2JpLJVrOH0Bf41wYCVa0hAcO5fu4hOIysxHS9sgv-k"
            ),
            Arrays.asList(
                new String[]{"Autonomia", "Até 40 horas"},
                new String[]{"Cancelamento", "ANC Híbrido inteligente"},
                new String[]{"Conexão", "Bluetooth 5.2 / P2"},
                new String[]{"Drivers", "40mm de Neodímio"}
            )
        );

        // 4. Cafeteira
        Product coffeemaker = createProduct("coffeemaker-1",
            "Cafeteira Expresso Automática 1.5L",
            new BigDecimal("239.00"), new BigDecimal("399.00"), 40,
            "casa", true,
            "Desfrute de expressos perfeitos e cremosos no conforto de sua casa. Com bomba de pressão profissional de 15 bar, reservatório de 1.5 litros e bico vaporizador para leite.",
            new BigDecimal("4.5"), "+300 vendidos", false,
            null, null, null, null,
            Arrays.asList(
                "https://lh3.googleusercontent.com/aida-public/AB6AXuAgAQVE5pj2AVHGR3MbVtk_hgOE7mRIZ6TUTl2wGQeWXCFx8PbJrXtp0jVZClak4UT_SmLisNUg41iMfN5XfXqqrahhgbRfyBkqLVYVCKLEb3pwELzbX1Sr_R34p6fjXSW3hM3WR-H370g_yZ-9-Xn-6oKTGzdYvcb-aFR5LRcaTcorKb-MfsBwf1b7Dhm8_EY45SvRUhZdXbj-bs1XiE_MnoR27987Yhg4v6w4TYORwPuiw44i1uJ18g_P9EWhA88jJonh__bC1jpO"
            ),
            Arrays.asList(
                new String[]{"Pressão", "15 Bar"},
                new String[]{"Capacidade", "1.5 Litros"},
                new String[]{"Potência", "1050W"},
                new String[]{"Bebidas", "Café Expresso, Cappuccino, Latte"}
            )
        );

        // 5. Tênis
        Product sneaker = createProduct("sneaker-1",
            "Tênis Performance Running Ultra",
            new BigDecimal("494.10"), new BigDecimal("549.00"), 10,
            "esporte", true,
            "Amortecimento responsivo que devolve energia a cada passada. Desenvolvido com malha respirável de alta durabilidade e solado antiderrapante em borracha vulcanizada.",
            new BigDecimal("4.6"), "+800 vendidos", false,
            null, null, null, null,
            Arrays.asList(
                "https://lh3.googleusercontent.com/aida-public/AB6AXuDk4BM5oIVUJ_b6mzSJ7bGq1YxU0FpI7M_gMc3NDGEC_bynvlaU2Ta4CRKw2kcgMadB_dfRUhCCMftWkPK1swINwvxWKyBhExk9uYNUW0ICDWOFUkbwoh0ux-FyyVeoIXCZzegui7mp2xtLGrctYNhszFaVG4kGDC4uKhqsxYYXo98R7eiCPe1gqv5xgmLzIpmiljbnq6MnmJzeXUIiC2kcTSynBOncpKYPF_2UJPC-H1RBMik3qLNTSX-BH5IHFmNeWLwq6nX6nQFI"
            ),
            Arrays.asList(
                new String[]{"Indicação", "Corrida / Treino"},
                new String[]{"Drop", "8mm"},
                new String[]{"Material", "Mesh respirável e TPU"},
                new String[]{"Amortecimento", "UltraBounce EVA Foam"}
            )
        );

        // 6. Liquidificador
        Product blender = createProduct("blender-1",
            "Liquidificador Turbo Power 1200W",
            new BigDecimal("189.90"), new BigDecimal("249.00"), 24,
            "casa", true,
            "Potência turbo de 1200W ideal para triturar gelo, frutas congeladas e massas com facilidade. Copo super resistente de 3 litros de capacidade útil com facas de aço inox afiadíssimas.",
            new BigDecimal("4.8"), "+2000 vendidos", false,
            null, null, null, null,
            Arrays.asList(
                "https://lh3.googleusercontent.com/aida-public/AB6AXuCbjZANAO7cegeyn6tuoGZFkdF8WoVTg4vqWVk6PgQKMhiLlBj1TGokIlAOMISdQFkIz93JII6qXlTjCouAtX_KNL390yL545NXOoKfxfycjiMxYDJ2_rAJly5C5Rri962HP9zzOFR36b9rvlhKfi1omf0M6PJVBThTaRREfZsxTu8yuwl9bqOeHqj9L_jCdoI0JxfJty7rERjBchStvDPu9rXiWjBaBqzo8_diSTqzvNoxvJJqvSustQ39hjJrt_GEpGAyKMi9awc1"
            ),
            Arrays.asList(
                new String[]{"Potência", "1200W"},
                new String[]{"Capacidade", "3.0 Litros"},
                new String[]{"Velocidades", "12 + Função Pulsar"},
                new String[]{"Material do Copo", "San Cristal livre de BPA"}
            )
        );

        // 7. Cadeira
        Product chair = createProduct("chair-1",
            "Cadeira Office Ergonômica Premium",
            new BigDecimal("749.00"), new BigDecimal("999.00"), 25,
            "casa", true,
            "Perfeita para longas jornadas de trabalho ou estudo. Possui encosto ergonômico em tela Mesh respirável de alta resistência, ajuste pneumático de altura e suporte lombar adaptativo.",
            new BigDecimal("4.9"), "+500 vendidos", false,
            null, null, null, null,
            Arrays.asList(
                "https://lh3.googleusercontent.com/aida-public/AB6AXuCPIQeWDS4rVLFTnQZfYA7ssrntTGxGIFiCOQqk1wz6NCBgtOQqIS4bKlxLWs5hj_9ojUtLyZOBUg9qw9f3h09ptLaxWrSXSiKpewNXU5rgLCKMREuYftucbuSb9m5Nlzg5Jm865XEjy2HVbCwTT7dVRybFW4Y0-RBtrUogQEeXX3SnNQorHzstU97iwWleVodxzuon4_DyK7Hgq3qc8lufKmU9RwxLaMK5JaFcT7vXvJ4lBgBYz8MvRIMcLlQIxmthqPtR5ykOfrTq"
            ),
            Arrays.asList(
                new String[]{"Estrutura", "Aço cromado e nylon reforçado"},
                new String[]{"Reclinável", "Sistema Relax até 120°"},
                new String[]{"Suporte", "Até 120kg"},
                new String[]{"Apoio de Braço", "Ajustável 3D"}
            )
        );

        // 8. Teclado Mecânico
        Product keyboard = createProduct("keyboard-1",
            "Teclado Mecânico RGB Hot-Swappable",
            new BigDecimal("299.00"), new BigDecimal("350.00"), 14,
            "games", true,
            "Estilo retro-moderno com keycaps premium em tons de cinza e amarelo. Teclado compacto com retroiluminação RGB dinâmica, switches mecânicos marrons de excelente feedback tátil e conexão via cabo espiral premium.",
            new BigDecimal("4.8"), "+450 vendidos", true,
            null, null, null, null,
            Arrays.asList(
                "https://lh3.googleusercontent.com/aida-public/AB6AXuCmieI1jz_prLSSwM8auwC7IN2o1YGiDAHUhoJvwjgRFMJfH3ZEGKYl_4uX43Fn0abkr2fKRgzX2q0oi6C7TwZUS33IoqRZ6crvr4rgvtBuVptTlxg-Omb_JcLWTFvV0s6Y8vV7LnOE65A3S76nbOfU6I5EK8FwFtcq_CM6hB_uPlTlc5dePvKWhMhuQrYkxbHyP0OvWMauVCuUbkIxPrWzNBeKqSrZLJ67xWESgkjfZ92ZJRbk-zedSmFKV8orhd-bykYB6eliYPJW"
            ),
            Arrays.asList(
                new String[]{"Switches", "Outemu Brown Mecânico"},
                new String[]{"Formato", "Compacto 75%"},
                new String[]{"Cabo", "Espiral USB-C Removível"},
                new String[]{"Layout", "ABNT2 ou ANSI"}
            )
        );

        // 9. Monitor Gamer
        Product monitor = createProduct("monitor-1",
            "Monitor Gamer 27\" 165Hz IPS QHD",
            new BigDecimal("1599.00"), new BigDecimal("2199.00"), 27,
            "games", true,
            "Experiencie jogos com fluidez extrema e cores vibrantes. O painel IPS de 27 polegadas com resolução QHD (2560x1440) e taxa de atualização de 165Hz garante imagens nítidas e sem tearing.",
            new BigDecimal("4.7"), "+350 vendidos", true,
            "Líder Gold", "4.7/5", "+10mil", "< 48h",
            Arrays.asList(
                "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=800&q=80",
                "https://images.unsplash.com/photo-1593640408182-31c228b42b44?w=800&q=80"
            ),
            Arrays.asList(
                new String[]{"Tamanho", "27 polegadas"},
                new String[]{"Resolução", "QHD 2560x1440"},
                new String[]{"Taxa de Atualização", "165Hz"},
                new String[]{"Painel", "IPS Anti-Reflexo"},
                new String[]{"Tempo de Resposta", "1ms (MPRT)"}
            )
        );

        // 10. Mochila Notebook
        Product backpack = createProduct("backpack-1",
            "Mochila Executiva para Notebook 15.6\"",
            new BigDecimal("189.90"), new BigDecimal("259.00"), 27,
            "moda", true,
            "Mochila profissional com compartimento acolchoado para notebook até 15.6\", porta USB integrada para carregamento e design resistente à água. Ideal para o dia a dia corporativo e viagens.",
            new BigDecimal("4.6"), "+1200 vendidos", false,
            null, null, null, null,
            Arrays.asList(
                "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=800&q=80",
                "https://images.unsplash.com/photo-1491553895911-0055eca6402d?w=800&q=80"
            ),
            Arrays.asList(
                new String[]{"Capacidade", "30 Litros"},
                new String[]{"Material", "Poliéster 900D impermeável"},
                new String[]{"Compartimentos", "6 bolsos organizadores"},
                new String[]{"Notebook", "Até 15.6 polegadas"}
            )
        );

        // 11. Bicicleta Elétrica
        Product ebike = createProduct("ebike-1",
            "Bicicleta Elétrica Urbana 350W",
            new BigDecimal("3299.00"), new BigDecimal("4500.00"), 27,
            "esporte", true,
            "Mobilidade urbana inteligente com motor elétrico de 350W e bateria de lítio removível com autonomia de até 60km. Freios a disco hidráulicos e display LCD integrado.",
            new BigDecimal("4.8"), "+150 vendidos", true,
            "Líder Platinum", "4.9/5", "+5mil", "< 72h",
            Arrays.asList(
                "https://images.unsplash.com/photo-1571068316344-75bc76f77890?w=800&q=80",
                "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=800&q=80"
            ),
            Arrays.asList(
                new String[]{"Motor", "350W Brushless"},
                new String[]{"Autonomia", "Até 60km"},
                new String[]{"Bateria", "36V 10Ah Lítio removível"},
                new String[]{"Velocidade Máx", "25 km/h"},
                new String[]{"Freios", "Disco hidráulico"}
            )
        );

        // 12. Perfume
        Product perfume = createProduct("perfume-1",
            "Perfume Masculino Intense 100ml EDP",
            new BigDecimal("249.90"), new BigDecimal("350.00"), 29,
            "moda", true,
            "Fragrância masculina intensa e sofisticada com notas de madeira, âmbar e especiarias. Longa duração de até 12 horas. Frasco elegante em vidro com acabamento premium.",
            new BigDecimal("4.5"), "+600 vendidos", false,
            null, null, null, null,
            Arrays.asList(
                "https://images.unsplash.com/photo-1541643600914-78b084683702?w=800&q=80",
                "https://images.unsplash.com/photo-1592945403244-b3fbafd7f539?w=800&q=80"
            ),
            Arrays.asList(
                new String[]{"Volume", "100ml"},
                new String[]{"Concentração", "Eau de Parfum (EDP)"},
                new String[]{"Notas de Topo", "Bergamota, Pimenta Rosa"},
                new String[]{"Notas de Base", "Sândalo, Âmbar, Musgo"}
            )
        );

        // Salvar todos os produtos
        List<Product> allProducts = Arrays.asList(camera, smartwatch, headphone, coffeemaker,
            sneaker, blender, chair, keyboard, monitor, backpack, ebike, perfume);
        productRepository.saveAll(allProducts);

        // =====================
        // USUÁRIO PADRÃO (comprador de demonstração)
        // =====================
        User user = User.builder()
            .name("Ricardo Oliveira")
            .email("ricardo@marketmax.com")
            .passwordHash(passwordEncoder.encode("demo123"))
            .role("USER")
            .active(true)
            .level("Nível Platinum")
            .memberSince("Out 2021")
            .avatarUrl("https://lh3.googleusercontent.com/aida-public/AB6AXuAsPTHkkGjoQHknLuL4LxvKY-m5FXAdGjHJVJdz3hhqVLhlIuKzaPr_KkVVy0pUNfeHzbmMXnpj1cDI92U4C8L_ouW28cyVrS2ruKpzeKq-dP2DxxDeG5eerj7Bw-PEqk3EWwvg8hJIRJpHzRXgXIEEmEU0zcujf1465TSoW-Yk29bfvgfMpi-UZ5NN9ArwuM38cISgoctA3oLZjUGrE-ODJkmHwjzykfP8cRORnSJkf-7F_Tkz4V3j0BlDfY1T7s0rayeRpwvBd9uS")
            .couponsCount(12)
            .coinsCount(850)
            .salesCount(4)
            .build();

        // Adicionar favoritos
        user.getFavorites().add(camera);
        user.getFavorites().add(smartwatch);

        userRepository.save(user);

        // =====================
        // USUÁRIO ADMINISTRADOR
        // =====================
        User admin = User.builder()
            .name("Administrador MarketMax")
            .email("admin@marketmax.com")
            .passwordHash(passwordEncoder.encode("admin123"))
            .role("ADMIN")
            .active(true)
            .level("Administrador")
            .memberSince("Jan 2021")
            .couponsCount(0)
            .coinsCount(0)
            .salesCount(0)
            .build();
        userRepository.save(admin);

        log.info("Usuário de demonstração: ricardo@marketmax.com / demo123");
        log.info("Usuário administrador: admin@marketmax.com / admin123");

        // =====================
        // CARRINHO INICIAL
        // =====================
        CartItem cartItem1 = CartItem.builder()
            .user(user)
            .product(headphone)
            .quantity(1)
            .build();
        CartItem cartItem2 = CartItem.builder()
            .user(user)
            .product(keyboard)
            .quantity(2)
            .build();
        cartItemRepository.saveAll(Arrays.asList(cartItem1, cartItem2));

        // =====================
        // PEDIDOS INICIAIS
        // =====================
        Order order1 = Order.builder()
            .id("order-101")
            .user(user)
            .orderDate(LocalDateTime.now().minusDays(30))
            .status("delivered")
            .totalAmount(new BigDecimal("1249.00"))
            .build();

        OrderItem oi1 = OrderItem.builder()
            .order(order1)
            .productId("headphone-1")
            .quantity(1)
            .title("Headphone Premium Wireless Noise Cancelling")
            .priceAtTime(new BigDecimal("1249.00"))
            .imageUrl("https://lh3.googleusercontent.com/aida-public/AB6AXuAtjOs6Ww3NC2rD-EG6B4SzcQbGbSvoi0ew6x9xRGlYlguYrqoDknIQwRX8wHiX41LuHloh24wIHevI-srRqNNLFxU-qbptX6YRg0Op0l3HLoQZqtFdutHzYrYiNBpXi5RaQgADC7ztfbKX8JjfBHIBDODcCbva1o80fhMof6rg_6iZRApMCXdOKwin2PDnz4oW9ko_7U_bkyoa8So5d1e07O_qPr-AWHO2-Gi1ziudEEvepWqyFgGwPpDpxQ7daxyninDkv9Txr7WL")
            .build();
        order1.getItems().add(oi1);

        Order order2 = Order.builder()
            .id("order-102")
            .user(user)
            .orderDate(LocalDateTime.now().minusDays(1))
            .status("shipped")
            .totalAmount(new BigDecimal("899.90"))
            .build();

        OrderItem oi2 = OrderItem.builder()
            .order(order2)
            .productId("chair-1")
            .quantity(1)
            .title("Cadeira Office Ergonômica Premium")
            .priceAtTime(new BigDecimal("899.90"))
            .imageUrl("https://lh3.googleusercontent.com/aida-public/AB6AXuCPIQeWDS4rVLFTnQZfYA7ssrntTGxGIFiCOQqk1wz6NCBgtOQqIS4bKlxLWs5hj_9ojUtLyZOBUg9qw9f3h09ptLaxWrSXSiKpewNXU5rgLCKMREuYftucbuSb9m5Nlzg5Jm865XEjy2HVbCwTT7dVRybFW4Y0-RBtrUogQEeXX3SnNQorHzstU97iwWleVodxzuon4_DyK7Hgq3qc8lufKmU9RwxLaMK5JaFcT7vXvJ4lBgBYz8MvRIMcLlQIxmthqPtR5ykOfrTq")
            .build();
        order2.getItems().add(oi2);

        orderRepository.saveAll(Arrays.asList(order1, order2));

        // =====================
        // NOTIFICAÇÕES INICIAIS
        // =====================
        Notification notif1 = Notification.builder()
            .id("notif-1")
            .user(user)
            .title("Nível Platinum Alcançado!")
            .message("Parabéns! Suas compras frequentes garantiram frete grátis liberado e cupons exclusivos.")
            .createdAt(LocalDateTime.now().minusDays(1))
            .isRead(false)
            .build();

        Notification notif2 = Notification.builder()
            .id("notif-2")
            .user(user)
            .title("Seu cupom de R$ 50 expirando em breve")
            .message("Aproveite seu cupom de boas-vindas nas categorias Eletro e Games hoje mesmo.")
            .createdAt(LocalDateTime.now().minusDays(2))
            .isRead(true)
            .build();

        Notification notif3 = Notification.builder()
            .id("notif-3")
            .user(user)
            .title("Promoção Relâmpago: até 60% OFF em Eletro!")
            .message("Câmeras, smartwatches e headphones com desconto por tempo limitado. Aproveite!")
            .createdAt(LocalDateTime.now().minusHours(3))
            .isRead(false)
            .build();

        notificationRepository.saveAll(Arrays.asList(notif1, notif2, notif3));

        log.info("Banco de dados inicializado com {} produtos, 1 usuário, 2 pedidos e 3 notificações.", allProducts.size());
    }

    private void seedCategories() {
        if (categoryRepository.count() > 0) {
            return;
        }
        log.info("Inicializando categorias padrão...");
        List<Category> categories = Arrays.asList(
            Category.builder().id("eletro").name("Eletrônicos").icon("📱").displayOrder(1).build(),
            Category.builder().id("casa").name("Casa e Cozinha").icon("🏠").displayOrder(2).build(),
            Category.builder().id("moda").name("Moda").icon("👟").displayOrder(3).build(),
            Category.builder().id("esporte").name("Esportes").icon("🏋️").displayOrder(4).build(),
            Category.builder().id("beleza").name("Beleza e Cuidados").icon("💄").displayOrder(5).build(),
            Category.builder().id("brinquedos").name("Brinquedos").icon("🧸").displayOrder(6).build(),
            Category.builder().id("livros").name("Livros").icon("📚").displayOrder(7).build(),
            Category.builder().id("automotivo").name("Automotivo").icon("🚗").displayOrder(8).build(),
            Category.builder().id("games").name("Games").icon("🎮").displayOrder(9).build()
        );
        categoryRepository.saveAll(categories);
    }

    private Product createProduct(String id, String title, BigDecimal price, BigDecimal originalPrice,
                                   Integer discount, String category, Boolean freeShipping,
                                   String description, BigDecimal rating, String salesCountText,
                                   Boolean isNew, String sellerReputation, String sellerRating,
                                   String sellerSales, String sellerPosting,
                                   List<String> imageUrls, List<String[]> specPairs) {

        Product product = Product.builder()
            .id(id)
            .title(title)
            .price(price)
            .originalPrice(originalPrice)
            .discount(discount)
            .category(category)
            .freeShipping(freeShipping)
            .description(description)
            .rating(rating)
            .salesCountText(salesCountText)
            .isNew(isNew)
            .sellerReputation(sellerReputation)
            .sellerRating(sellerRating)
            .sellerSales(sellerSales)
            .sellerPosting(sellerPosting)
            .build();

        for (int i = 0; i < imageUrls.size(); i++) {
            ProductImage img = ProductImage.builder()
                .product(product)
                .imageUrl(imageUrls.get(i))
                .displayOrder(i)
                .build();
            product.getImages().add(img);
        }

        for (String[] spec : specPairs) {
            ProductSpec s = ProductSpec.builder()
                .product(product)
                .specKey(spec[0])
                .specValue(spec[1])
                .build();
            product.getSpecs().add(s);
        }

        return product;
    }
}
