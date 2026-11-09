import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:timezone/data/latest.dart' as tzdata;

import 'l10n/app_localizations.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  tzdata.initializeTimeZones();
  runApp(const ProviderScope(child: AqimSalatTvApp()));
}

class AqimSalatTvApp extends StatelessWidget {
  const AqimSalatTvApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      onGenerateTitle: (context) => AppLocalizations.of(context).appTitle,
      debugShowCheckedModeBanner: false,
      supportedLocales: AppLocalizations.supportedLocales,
      localizationsDelegates: const [
        AppLocalizations.delegate,
        GlobalMaterialLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate,
        GlobalCupertinoLocalizations.delegate,
      ],
      theme: ThemeData(
        brightness: Brightness.dark,
        scaffoldBackgroundColor: const Color(0xFF071D18),
        colorScheme: ColorScheme.fromSeed(
          seedColor: const Color(0xFF0F684B),
          brightness: Brightness.dark,
          primary: const Color(0xFF1B8A63),
          secondary: const Color(0xFFD4AF37),
        ),
        textTheme: const TextTheme(
          headlineLarge: TextStyle(fontSize: 42, fontWeight: FontWeight.w700),
          titleLarge: TextStyle(fontSize: 30, fontWeight: FontWeight.w600),
          bodyLarge: TextStyle(fontSize: 24),
        ),
        visualDensity: VisualDensity.standard,
        useMaterial3: true,
      ),
      home: const TvHomeScreen(),
    );
  }
}

class TvHomeScreen extends StatefulWidget {
  const TvHomeScreen({super.key});

  @override
  State<TvHomeScreen> createState() => _TvHomeScreenState();
}

class _TvHomeScreenState extends State<TvHomeScreen> {
  int _selectedIndex = 0;

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context);
    final actions = <String>[
      l10n.chooseMosque,
      l10n.cityOnly,
      l10n.settings,
    ];

    return SafeArea(
      minimum: const EdgeInsets.symmetric(horizontal: 56, vertical: 32),
      child: Scaffold(
        body: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Row(
              children: [
                const Icon(Icons.mosque, size: 42, color: Color(0xFFD4AF37)),
                const SizedBox(width: 16),
                Expanded(
                  child: Text(
                    l10n.appTitle,
                    style: Theme.of(context).textTheme.headlineLarge,
                  ),
                ),
                Text(
                  l10n.setupRequired,
                  style: Theme.of(context).textTheme.titleLarge,
                ),
              ],
            ),
            const Spacer(),
            Center(
              child: Column(
                children: [
                  const Icon(Icons.location_city, size: 92, color: Color(0xFFD4AF37)),
                  const SizedBox(height: 24),
                  Text(
                    l10n.welcomeTitle,
                    textAlign: TextAlign.center,
                    style: Theme.of(context).textTheme.headlineLarge,
                  ),
                  const SizedBox(height: 12),
                  Text(
                    l10n.welcomeBody,
                    textAlign: TextAlign.center,
                    style: Theme.of(context).textTheme.bodyLarge,
                  ),
                ],
              ),
            ),
            const Spacer(),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: List.generate(actions.length, (index) {
                return Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 12),
                  child: Focus(
                    onFocusChange: (focused) {
                      if (focused) setState(() => _selectedIndex = index);
                    },
                    child: SizedBox(
                      width: 250,
                      height: 76,
                      child: FilledButton(
                        autofocus: index == 0,
                        onPressed: () => setState(() => _selectedIndex = index),
                        style: FilledButton.styleFrom(
                          backgroundColor: _selectedIndex == index
                              ? const Color(0xFFD4AF37)
                              : const Color(0xFF174638),
                          foregroundColor: _selectedIndex == index
                              ? const Color(0xFF10251E)
                              : Colors.white,
                          textStyle: const TextStyle(fontSize: 22, fontWeight: FontWeight.w600),
                        ),
                        child: Text(actions[index]),
                      ),
                    ),
                  ),
                );
              }),
            ),
          ],
        ),
      ),
    );
  }
}
