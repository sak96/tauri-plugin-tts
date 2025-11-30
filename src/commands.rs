use tauri::{command, AppHandle, Runtime};

use crate::models::*;
use crate::Result;
use crate::TtsExt;

#[command]
pub(crate) async fn speak<R: Runtime>(app: AppHandle<R>, args: SpeakArgs) -> Result<()> {
    app.tts().speak(args)
}

#[command]
pub(crate) async fn stop<R: Runtime>(app: AppHandle<R>) -> Result<()> {
    app.tts().stop()
}

#[command]
pub(crate) async fn set_voice<R: Runtime>(app: AppHandle<R>, voice: String) -> Result<()> {
    app.tts().set_voice(voice)
}

#[command]
pub(crate) async fn get_all_voices<R: Runtime>(app: AppHandle<R>) -> Result<GetVoicesResponse> {
    app.tts().get_all_voices()
}
