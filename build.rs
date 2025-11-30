const COMMANDS: &[&str] = &["speak", "stop", "set_voice", "get_all_voices"];

fn main() {
    tauri_plugin::Builder::new(COMMANDS)
        .android_path("android")
        .ios_path("ios")
        .build();
}
