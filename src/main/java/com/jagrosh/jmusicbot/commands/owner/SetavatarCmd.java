/*
 * Copyright 2017 John Grosh <john.a.grosh@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jagrosh.jmusicbot.commands.owner;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.OwnerCommand;
import com.jagrosh.jmusicbot.utils.OtherUtil;
import net.dv8tion.jda.api.entities.Icon;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class SetavatarCmd extends OwnerCommand {

  public SetavatarCmd(Bot bot) {
    super(bot);
    this.name = "setavatar";
    this.help = "устанавливает аватар для бота";
    this.arguments = "<url>";
    this.aliases = bot.getConfig().getAliases(this.name);
    this.guildOnly = false;
  }

  @Override
  protected void execute(SlashCommandEvent event) {
    event.deferReply().queue();

  }

  @Override
  protected void execute(CommandEvent event) {
    String url;
    if (event.getArgs().isEmpty())
      if (!event.getMessage().getAttachments().isEmpty() && event.getMessage().getAttachments().get(0).isImage())
        url = event.getMessage().getAttachments().get(0).getUrl();
      else url = null;
    else url = event.getArgs();
    InputStream s = OtherUtil.imageFromUrl(url);
    if (s == null) {
      event.reply(event.getClient().getError() + " Неизвестный URL");
    } else {
      try {
        event.getSelfUser().getManager().setAvatar(Icon.from(s)).queue(v -> event.reply(event.getClient().getSuccess() + " Успешно изменен аватар."), t -> event.reply(event.getClient().getError() + " Не удалось установить аватар."));
      } catch (IOException e) {
        event.reply(event.getClient().getError() + " Не получилось загрузить аватар с данного URL.");
      }
    }
  }
}
