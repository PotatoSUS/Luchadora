package cookiedragon.luchadora.module.impl.ui.elements.clickgui;

import cookiedragon.luchadora.event.api.EventDispatcher;
import cookiedragon.luchadora.event.api.Subscriber;
import cookiedragon.luchadora.event.luchadora.ModuleInitialisationEvent;
import cookiedragon.luchadora.module.AbstractModule;
import cookiedragon.luchadora.module.Category;
import cookiedragon.luchadora.module.ModuleManager;
import cookiedragon.luchadora.module.impl.ui.AbstractHudElement;
import cookiedragon.luchadora.util.Key;
import cookiedragon.luchadora.util.RenderUtils;
import cookiedragon.luchadora.util.Vec2f;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author cookiedragon234 21/Dec/2019
 */
@AbstractModule.Declaration(name = "Category", description = "", category = Category.UI, defaultOn = true)
public class CategoryElement extends AbstractHudElement
{
	public final GuiModule guiModule;
	private final Category category;
	private Vec2f titleSize = new Vec2f(0,0);
	private Vec2f modulePosition = new Vec2f(0,0);
	private boolean collapsed = false;
	
	private ArrayList<ModuleElement> moduleElements;
	
	public CategoryElement(Category category, GuiModule guiModule)
	{
		super();
		setName(category.displayName);
		this.category = category;
		this.guiModule = guiModule;
		
		EventDispatcher.subscribe(this);
	}
	
	@Subscriber
	private void onModuleInitialisation(ModuleInitialisationEvent.Post event)
	{
		EventDispatcher.unsubscribe(this);
		
		moduleElements = new ArrayList<>();
		for (AbstractModule module : ModuleManager.getModules())
		{
			moduleElements.add(new ModuleElement(module, this));
		}
	}
	
	@Override
	public boolean keyTyped(Key key)
	{
		super.keyTyped(key);
		if (!shouldRender()) return false;
		
		if (!collapsed)
		{
			for (ModuleElement moduleElement : moduleElements)
			{
				if (moduleElement.keyTyped(key))
					return true;
			}
		}
		return false;
	}
	
	@Override
	public void render(Vec2f mousePos)
	{
		super.render(mousePos);
		if (!shouldRender()) return;
		
		titleSize = new Vec2f(
			100,
			mc.fontRenderer.getFontHeight() + 3
		);
		
		draggableSize = titleSize;
		
		RenderUtils.renderRectangle(
			position.x,
			position.y,
			position.x + titleSize.x,
			position.y + titleSize.y,
			guiModule.primaryColour.getValue().getRGB()
		);
		
		RenderUtils.renderOutline(
			position.x,
			position.y,
			position.x + titleSize.x,
			position.y + titleSize.y,
			new Color(0,0,0, 50).getRGB()
		);
		
		mc.fontRenderer.drawCenteredString(
			this.getName(),
			position.x,
			position.y + 2,
			titleSize.x,
			guiModule.textColour.getValue().getRGB()
		);
		
		//
		
		modulePosition = new Vec2f(
			position.x,
			position.y + titleSize.y
		);
		
		if (!collapsed)
		{
			for (ModuleElement module : moduleElements)
			{
				if (module.getCategory() == this.category)
				{
					module.modulePosition = modulePosition.copy();
					
					module.render(mousePos);
					
					modulePosition.y += module.moduleSize.y;
				}
			}
		}
		
		size.x = 100;
		size.y = modulePosition.y - position.y;
	}
	
	
	@Override
	public boolean mouseClick(Vec2f mousePos, int mouseID)
	{
		super.mouseClick(mousePos, mouseID);
		
		if (isMouseOver(mousePos))
		{
			if (mouseID == 1)
			{
				collapsed = !collapsed;
				return true;
			}
		}
		
		for (ModuleElement moduleElement : moduleElements)
		{
			if (moduleElement.mouseClick(mousePos, mouseID))
				return true;
		}
		
		return false;
	}
	
	@Override
	public boolean mouseRelease(Vec2f mousePos, int mouseID)
	{
		super.mouseRelease(mousePos, mouseID);
		
		for (ModuleElement moduleElement : moduleElements)
		{
			if (moduleElement.mouseRelease(mousePos, mouseID))
				return true;
		}
		
		return false;
	}
	
	@Override
	public boolean mouseClickMove(Vec2f mousePos, int mouseID)
	{
		super.mouseClickMove(mousePos, mouseID);
		
		for (ModuleElement moduleElement : moduleElements)
		{
			if (moduleElement.mouseClickMove(mousePos, mouseID))
				return true;
		}
		
		return false;
	}
	
	@Override
	public boolean shouldRender()
	{
		return mc.getCurrentScreen() instanceof EditHudGui && (this.getEnabled().getValue() || this.getName().equals(this.category.displayName));
	}
}